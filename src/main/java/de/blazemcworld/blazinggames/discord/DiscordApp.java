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
import de.blazemcworld.blazinggames.events.ChatEventListener;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import de.blazemcworld.blazinggames.utils.TextUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiscordApp extends ListenerAdapter {
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

    public static void messageHook(Player player, Component message) {
        if (app == null) return;
        app.sendDiscordMessage(player, TextUtils.stripColorCodes(TextUtils.componentToString(message)));
    }

    /**
     * Sends a notification to the channel.
     * @param notification The notification to send
     */
    public static void send(DiscordNotification notification) {
        if (app == null) return; // might be disabled or failed to start
        app.notify(notification);
    }

    private static DiscordApp app = null;
    private final JDA jda;
    private final StandardGuildMessageChannel channel;
    private final StandardGuildMessageChannel consoleChannel;
    private long lastMessageId = 0;
    private final WebhookClient client;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ReentrantLock lock = new ReentrantLock();
    private DiscordApp(AppConfig config) {
        if (config.token() == null) {
            throw new IllegalArgumentException("app token is not defined");
        } else if (config.webhookUrl() == null) {
            throw new IllegalArgumentException("app webhook is not defined");
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

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
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

    private void sendDiscordMessage(Player player, String content) {
        PlayerConfig config = PlayerConfig.forPlayer(player.getUniqueId());
        String out;
        if (ChatEventListener.meFormat(content) != null) {
            out = config.buildNameStringShort(player.getName()) + " " + ChatEventListener.meFormat(content);
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

        WebhookMessage message = new WebhookMessageBuilder()
                .setUsername(config.buildNameString(player.getName(), player.isOp()))
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
            messageSegment = Component.text(": ")
                    .color(NamedTextColor.WHITE)
                    .append(TextUtils.colorCodeParser(TextUtils.stringToComponent(content).color(NamedTextColor.WHITE)));
        } else if (attachmentsRaw.length > 0) {
            messageSegment = Component.text(" sent attachments").color(NamedTextColor.WHITE);
        } else if (stickersRaw.length > 0) {
            messageSegment = Component.text(" sent stickers").color(NamedTextColor.WHITE);
        } else {
            messageSegment = Component.text(" sent something").color(NamedTextColor.WHITE);
        }

        Bukkit.broadcast(Component.text()
                .append(Component.text("[DISCORD] ")
                        .color(TextColor.color(0x2d4386)))
                .append(Component.text(member.getEffectiveName())
                        .color(TextColor.color(member.getColorRaw())))
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
