/*
 * Copyright 2025 The Blazing Games Maintainers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.blazemcworld.blazinggames.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.discord.commands.*;
import de.blazemcworld.blazinggames.events.ChatEventListener;
import de.blazemcworld.blazinggames.utils.DisplayTag;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.PlayerInfo;
import de.blazemcworld.blazinggames.utils.TextUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.function.Function;

import javax.annotation.Nonnull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiscordApp extends ListenerAdapter {
    private final List<ICommand> commands = List.of(

    );
    private final List<ICommand> whitelistCommands = List.of(
        new WhitelistCommand(), new UnlinkCommand(), new SetPrimaryCommand(), new LinksCommand(), new SyncCommand()
    );


    /**
     * Starts the bot. This operation blocks the thread.
     */
    public static void init(AppConfig config) throws IllegalArgumentException {
        if (app != null) return;
        app = new DiscordApp(config);
    }

    /**
     * Stop the bot. This operation blocks the thread.
     */
    public static void dispose() {
        if (app == null) return;
        app.stop();
        app = null;
    }

    public static void messageHook(Player player, String message, DisplayTag displayTag) {
        if (app == null) return;
        app.sendDiscordMessage(player, TextUtils.stripColorCodes(message), displayTag);
    }

    /**
     * Sends a notification to the channel.
     * @param notification The notification to send
     */
    public static void send(DiscordNotification notification) {
        if (app == null) return; // might be disabled or failed to start
        app.notify(notification);
    }

    public static boolean isWhitelistManaged() {
        if (app == null) return false;
        return app.whitelist != null;
    }

    public static WhitelistManagement getWhitelistManagement() {
        if (app == null) return null;
        return app.whitelist;
    }

    private static DiscordApp app = null;
    private final JDA jda;
    private final StandardGuildMessageChannel channel;
    private final StandardGuildMessageChannel consoleChannel;
    private long lastMessageId = 0;
    private final WebhookClient client;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ReentrantLock lock = new ReentrantLock();
    private final WhitelistManagement whitelist;
    private DiscordApp(AppConfig config) {
        if (config.token() == null) {
            throw new IllegalArgumentException("app token is not defined");
        } else if (config.webhookUrl() == null) {
            throw new IllegalArgumentException("app webhook is not defined");
        }

        if (config.managedWhitelist()) {
            this.whitelist = new WhitelistManagement();
        } else {
            this.whitelist = null;
        }

        jda = JDABuilder
                .createDefault(config.token())
                .addEventListeners(this)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        try {
            // block thread until JDA has started
            jda.awaitReady();

            this.channel = jda.getTextChannelById(config.channelId());
            if (this.channel == null) {
                stop();
                throw new IllegalArgumentException("channelId is not a valid channel");
            }
            this.consoleChannel = jda.getTextChannelById(config.consoleChannelId());
            if (this.consoleChannel == null) {
                stop();
                throw new IllegalArgumentException("consoleChannelId is not a valid channel");
            }

            BufferedInputStream logs = null;
            try {
                logs = new BufferedInputStream(new FileInputStream("logs/latest.log"));
                BufferedInputStream finalLogs = logs;
                Bukkit.getScheduler().scheduleSyncRepeatingTask(BlazingGames.get(), () -> {
                    try {
                        String s = new String(finalLogs.readAllBytes());
                        if (!s.isEmpty()) sendLogMessage(s);
                    } catch (IOException e) {
                        BlazingGames.get().log(e);
                    }
                }, 0, 20);
            } catch (FileNotFoundException ignored) {}
            this.client = WebhookClient.withUrl(config.webhookUrl());

            updateCommands();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void stop() {
        jda.shutdown();
        try {
            // block thread until JDA has stopped
            jda.awaitShutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ICommand> getAvailableCommands() {
        ArrayList<ICommand> available = new ArrayList<>(commands);
        if(whitelist != null) {
            available.addAll(whitelistCommands);
        }
        return available;
    }

    private void updateCommands() {
        CommandListUpdateAction commandsAction = jda.updateCommands();

        for(ICommand command : getAvailableCommands()) {
            command.register(commandsAction);
        }

        commandsAction.queue();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getId().equals(this.channel.getId())) {
            sendMinecraftMessage(Objects.requireNonNull(event.getMember()),
                    event.getMessage().getContentRaw(),
                    event.getMessage().getAttachments().toArray(new Message.Attachment[0]),
                    event.getMessage().getStickers().toArray(new StickerItem[0]));
        } else if (event.getChannel().getId().equals(this.consoleChannel.getId())) {
            lastMessageId = 0;
            String command = event.getMessage().getContentRaw();
            if (command.startsWith("/")) command = command.substring(1);
            String finalCommand = command;
            Bukkit.getScheduler().runTask(BlazingGames.get(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
        }
    }


    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            SlashCommandInteraction interaction = event.getInteraction();
            String name = interaction.getName();
            for(ICommand command : getAvailableCommands()) {
                if(command.name().equals(name)) {
                    command.handle(event);
                    return;
                }
            }
            event.reply("Somehow, you provided an invalid command.").setEphemeral(true).queue();
        } else {
            event.reply("Slash commands may only be used in guilds.").setEphemeral(true).queue();
        }
    }

    private void sendDiscordMessage(Player player, String content, DisplayTag displayTag) {
        String out;
        if (ChatEventListener.meFormat(content) != null) {
            out = displayTag.buildNameStringShort() + " " + ChatEventListener.meFormat(content);
        } else if (ChatEventListener.greentextFormat(content) != null) {
            StringBuilder builder = new StringBuilder();
            String[] parts = ChatEventListener.greentextFormat(content);
            for (String part : parts) {
                builder.append("\\> ").append(part).append("\n");
            }
            out = builder.toString().trim();
        } else {
            out = content;
        }

        if (isWhitelistManaged()) {
            out = getWhitelistManagement().formatMentionsMinecraftToDiscord(out);
        }

        WebhookMessage message = new WebhookMessageBuilder()
                .setUsername(displayTag.buildNameString())
                .setAvatarUrl("https://cravatar.eu/helmavatar/" + player.getUniqueId() + "/128.png")
                .setContent(out)
                .build();
        this.client.send(message);
    }

    private void sendLogMessage(String content) {
        executor.submit(() -> {
            try {
                lock.lock();
                if (lastMessageId == 0) {
                    Message last = consoleChannel.sendMessage("```\n\n```").complete();
                    lastMessageId = last.getIdLong();
                }
                Message last = consoleChannel.retrieveMessageById(lastMessageId).complete();
                String[] lines = content.split("\n");
                StringBuilder msgContent = new StringBuilder();
                int index = 0;
                boolean editedLast = false;
                for (String line : lines) {
                    index++;
                    if (!editedLast) {
                        if (last.getContentRaw().length() + msgContent.length() + line.length() + 5 < 2000) {
                            msgContent.append(line).append("\n");
                            if (index == lines.length) {
                                String oldContent = last.getContentRaw().substring(0, last.getContentRaw().length() - 4);
                                last.editMessage(oldContent + msgContent + "\n```").complete();
                            }
                        } else {
                            if (last.getContentRaw().length() + msgContent.length() + line.length() + 5 >= 2000 && index == lines.length) {
                                if (msgContent.isEmpty()) msgContent = new StringBuilder("```\n");
                                if (!msgContent.toString().startsWith("```\n")) msgContent = new StringBuilder("```\n" + msgContent);
                                msgContent.append(line).append("\n");
                                Message msg = consoleChannel.sendMessage(msgContent + "```").complete();
                                lastMessageId = msg.getIdLong();
                            } else {
                                if (msgContent.isEmpty()) msgContent = new StringBuilder("```\n");
                                String oldContent = last.getContentRaw().substring(0, last.getContentRaw().length() - 4);
                                last.editMessage(oldContent + msgContent + "\n```").complete();
                                msgContent = new StringBuilder("```\n" + line);
                                editedLast = true;
                            }
                        }
                    } else {
                        if (msgContent.length() + line.length() + 5 < 2000 && index != lines.length) {
                            msgContent.append(line).append("\n");
                        } else {
                            if (msgContent.isEmpty()) msgContent = new StringBuilder("```\n");
                            Message msg = consoleChannel.sendMessage(msgContent + "```").complete();
                            lastMessageId = msg.getIdLong();
                            msgContent = new StringBuilder("```\n" + line);
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        });
    }

    // https://stackoverflow.com/a/4247219
    private Component prettyName(String text) {
        Random random = new Random(text.hashCode()); // use hashCode for consistency
        final float hue = random.nextFloat();
        // Saturation between 0.1 and 0.3
        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        final Color color = Color.getHSBColor(hue, saturation, luminance);
        return Component.text(text).color(TextColor.color(color.getRGB()));
    }

    private <T> Component formatArrayIntoComponent(
            String title, T[] items, Function<T, Component> chatText,
            Function<T, Component> hoverText, Function<T, String> clickUrl
    ) {
        Component lBracket = Component.text("[").color(NamedTextColor.GRAY);
        Component rBracket = Component.text("]").color(NamedTextColor.GRAY);
        Component attachmentList = Arrays.stream(items).map(t -> lBracket.append(chatText.apply(t)).append(rBracket)
                        .hoverEvent(HoverEvent.showText(hoverText.apply(t)))
                        .clickEvent(ClickEvent.openUrl(clickUrl.apply(t)))
                        .color(NamedTextColor.GRAY))
                .reduce(Component.empty(), (textComponent, textComponent2) ->
                        Component.empty().equals(textComponent) ? textComponent.append(textComponent2) :
                        textComponent.append(Component.text(", ").color(NamedTextColor.WHITE))
                                .append(textComponent2)
                );
        return Component.newline().append(Component.text("↳ " + title + ": ").color(NamedTextColor.WHITE)).append(attachmentList);
    }

    private static Component createAttachmentDataString(Message.Attachment attachment) {
        Component description = (attachment.getDescription() == null) ?
                Component.text("No description (alt text) provided").decorate(TextDecoration.ITALIC) :
                Component.text(attachment.getDescription());
        Component metadata = Component.text(attachment.getContentType() + " - " + humanReadableByteCountBin(attachment.getSize()));
        Component spoiler = attachment.isSpoiler() ? Component.newline().append(Component.text(
                "This file is a spoiler!"
        ).color(NamedTextColor.RED)) : Component.empty();
        return Component.empty().append(description).appendNewline().append(metadata).append(spoiler).appendNewline().appendNewline().append(
                Component.text("Click to preview in browser")
        );
    }

    // https://stackoverflow.com/a/3758880
    // changed to use Int instead of Long
    public static String humanReadableByteCountBin(int bytes) {
        long absB = bytes == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    private Component getDisplayName(Member member) {
        Component discordName = Component.text(member.getEffectiveName())
                .color(TextColor.color(member.getColorRaw()))
                .hoverEvent(Component.text("Discord Display Name\nDiscord Username: "
                        + member.getUser().getName()).asHoverEvent());

        if(!isWhitelistManaged()) {
            return discordName;
        }

        DiscordUser user = whitelist.updateUser(member.getUser());

        if(user.favoriteAccount == null) {
            return discordName;
        }

        WhitelistedPlayer player = whitelist.getWhitelistedPlayer(user.favoriteAccount);
        PlayerInfo info = PlayerInfo.fromWhitelistedPlayer(player);

        if(info == null) {
            return discordName;
        }

        PlayerConfig config = PlayerConfig.forPlayer(info);
        return config.toDisplayTag(true).buildNameComponent();
    }

    private void sendMinecraftMessage(Member member, String content, Message.Attachment[] attachmentsRaw, Sticker[] stickersRaw) {
        Component attachments = (attachmentsRaw.length > 0) ? formatArrayIntoComponent(
                "Attachments", attachmentsRaw, attachment -> prettyName(attachment.getFileName()),
                DiscordApp::createAttachmentDataString, // fun fact: if that method is not static, intellij complains for some reason
                Message.Attachment::getUrl
        ) : Component.empty();

        Component stickers = (stickersRaw.length > 0) ? formatArrayIntoComponent(
                "Stickers", stickersRaw, sticker -> prettyName(sticker.getName()),
                sticker -> Component.text("Open Discord to view this sticker"),
                sticker -> ""
        ) : Component.empty();

        Component messageSegment;
        if (!content.isBlank()) {
            if (isWhitelistManaged()) {
                content = getWhitelistManagement().formatMentionsDiscordToMinecraft(content);
            }
            messageSegment = Component.text(": ")
                    .color(NamedTextColor.WHITE)
                    .append(TextUtils.colorCodeParser(content));
        } else if (attachmentsRaw.length > 0) {
            messageSegment = Component.text(" sent attachments").color(NamedTextColor.WHITE);
        } else if (stickersRaw.length > 0) {
            messageSegment = Component.text(" sent stickers").color(NamedTextColor.WHITE);
        } else {
            messageSegment = Component.text(" sent something").color(NamedTextColor.WHITE);
        }

        Bukkit.broadcast(Component.text()
                .append(Component.text("☁").hoverEvent(Component.text("Discord Message").asHoverEvent())
                        .color(TextColor.color(0x4E58DE)))
                .appendSpace()
                .append(getDisplayName(member))
                .append(messageSegment)
                .append(attachments)
                .append(stickers)
                .build());
    }

    private void notify(DiscordNotification notification) {
        channel.sendMessageEmbeds(List.of(notification.toEmbed()))
                .setSuppressedNotifications(true).queue();
    }
}
