package de.blazemcworld.blazinggames.discord;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.commands.DiscordWhitelistCommand;
import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.ArbitraryNameProvider;
import de.blazemcworld.blazinggames.data.name.UUIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import de.blazemcworld.blazinggames.utils.Pair;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;

public class WhitelistManagement {
    private final DataStorage<WhitelistedPlayer, UUID> whitelist = DataStorage.forClass(
        WhitelistManagement.class, "whitelist",
        new GsonStorageProvider<WhitelistedPlayer>(WhitelistedPlayer.class),
        new UUIDNameProvider(), new GZipCompressionProvider()
    );

    private final DataStorage<DiscordUser, String> discordUsers = DataStorage.forClass(
        WhitelistManagement.class, "discord",
        new GsonStorageProvider<DiscordUser>(DiscordUser.class),
        new ArbitraryNameProvider(), new GZipCompressionProvider()
    );

    private final Cache<String, Pair<String, UUID>> linkCodes = Caffeine
        .newBuilder()
        .maximumSize(1000L)
        .expireAfterWrite(Duration.ofMinutes(10L))
        .build();

    WhitelistManagement() {}

    public boolean isWhitelisted(UUID uuid) {
        return whitelist.hasData(uuid);
    }

    public DiscordUser updateUser(User user) {
        DiscordUser discordUser = getDiscordUser(user.getIdLong());
        if(discordUser == null) {
            return updateUser(user, null);
        }
        return updateUser(user, discordUser.favoriteAccount);
    }

    public DiscordUser updateUser(User user, UUID primary) {
        DiscordUser discordUser = new DiscordUser();
        discordUser.snowflake = user.getIdLong();
        discordUser.displayName = user.getGlobalName();
        discordUser.username = user.getName();
        if(primary != null) {
            discordUser.favoriteAccount = primary;
        }
        discordUsers.storeData(user.getId(), discordUser);
        return discordUser;
    }

    public WhitelistedPlayer addPlayer(UUID player, long discord) {
        WhitelistedPlayer whitelistedPlayer = new WhitelistedPlayer();
        whitelistedPlayer.discordUser = discord;
        whitelistedPlayer.uuid = player;
        whitelistedPlayer.whitelistedAt = System.currentTimeMillis();
        whitelist.storeData(player, whitelistedPlayer);
        return whitelistedPlayer;
    }

    public void updatePlayerLastKnownName(UUID player, String name) {
        WhitelistedPlayer whitelistedPlayer = getWhitelistedPlayer(player);

        if(whitelistedPlayer == null) return;

        whitelistedPlayer.lastKnownName = name;
        whitelist.storeData(player, whitelistedPlayer);
    }

    public boolean removePlayer(WhitelistedPlayer player) {
        return removePlayer(player, true);
    }

    public boolean removePlayer(WhitelistedPlayer player, boolean enforceWhitelist) {
        whitelist.deleteData(player.uuid);

        if(this == DiscordApp.getWhitelistManagement() && enforceWhitelist) {
            Bukkit.getScheduler().runTask(BlazingGames.get(), DiscordWhitelistCommand::enforceWhitelist);
        }

        DiscordUser user = getDiscordUser(player.discordUser);
        if(user == null) {
            return false;
        }

        if(!player.uuid.equals(user.favoriteAccount)) {
            return false;
        }

        List<WhitelistedPlayer> remaining = whitelist.queryForData((data) -> data.discordUser == user.snowflake);

        remaining.sort(Comparator.comparingLong((a) -> a.whitelistedAt));

        if(remaining.isEmpty()) {
            user.favoriteAccount = null;
        }
        else {
            user.favoriteAccount = remaining.getFirst().uuid;
        }

        discordUsers.storeData(Long.toUnsignedString(user.snowflake), user);

        return true;
    }
    
    public WhitelistedPlayer getWhitelistedPlayer(UUID uuid) {
        return whitelist.getData(uuid);
    }

    public WhitelistedPlayer getWhitelistedPlayer(String username) {
        List<WhitelistedPlayer> players = whitelist.queryForData((data) -> data.lastKnownName.equalsIgnoreCase(username));

        if(players.isEmpty()) {
            return null;
        }

        players.sort(Comparator.comparingLong((a) -> a.whitelistedAt));

        return players.getFirst();
    }

    public List<WhitelistedPlayer> getWhitelistedPlayersOfDiscordUser(DiscordUser user) {
        List<WhitelistedPlayer> players = whitelist.queryForData((data) -> data.discordUser == user.snowflake);

        players.sort(Comparator.comparingLong((a) -> a.whitelistedAt));

        return players;
    }

    public DiscordUser getDiscordUser(long snowflake) {
        return discordUsers.getData(String.valueOf(snowflake));
    }

    public List<DiscordUser> getAllKnownDiscordUsers() {
        return discordUsers.queryForData((a) -> true);
    }

    public String createLinkCode(String username, UUID user) {
        byte[] bytes = new byte[3];
        new Random().nextBytes(bytes);
        StringBuilder out = new StringBuilder();

        for (byte b : bytes) {
            out.append(String.format("%02x", b));
        }

        linkCodes.put(out.toString().toUpperCase(), new Pair<>(username, user));
        return out.toString();
    }

    public Pair<String, UUID> getLinkCodeUser(String code) {
        return linkCodes.getIfPresent(code.toUpperCase());
    }

    public void invalidateLinkCode(String code) {
        linkCodes.invalidate(code.toUpperCase());
    }

    // modified version of https://regex101.com/library/xA4cW8
    public static final Pattern minecraftMentionPattern = Pattern.compile("@(?<name>[a-zA-Z0-9_]{3,16})");
    public String formatMentionsMinecraftToDiscord(String input) {
        // matches: @Username
        // replace with: <@snowflake>, if known user

        Matcher matcher = minecraftMentionPattern.matcher(input);
        while (matcher.find()) {
            WhitelistedPlayer player = DiscordApp.getWhitelistManagement().getWhitelistedPlayer(matcher.group("name"));
            if (player != null) {
                DiscordUser user = DiscordApp.getWhitelistManagement().getDiscordUser(player.discordUser);
                if (user == null) continue;
                input = input.replace(matcher.group(), "<@" + user.snowflake + ">");
            }
        }

        return input;
    }

    // borrowed from https://www.sapphirejs.dev/docs/Guide/utilities/Discord_Utilities/UsefulRegexes/#userormember-mention-regex
    public static final Pattern discordMentionPattern = Pattern.compile("<@!?(?<id>\\d{17,20})>");
    public String formatMentionsDiscordToMinecraft(String input) {
        // matches: <@snowflake>
        // replace with: @Username

        Matcher matcher = discordMentionPattern.matcher(input);
        while (matcher.find()) {
            String snowflake = matcher.group("id");
            DiscordUser user = DiscordApp.getWhitelistManagement().getDiscordUser(Long.parseLong(snowflake));
            if (user != null && user.favoriteAccount != null) {
                WhitelistedPlayer player = DiscordApp.getWhitelistManagement().getWhitelistedPlayer(user.favoriteAccount);
                if (player == null) continue;
                input = input.replace(matcher.group(), "@" + player.lastKnownName);
            }
        }

        return input;
    }
}
