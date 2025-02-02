package de.blazemcworld.blazinggames.discord;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.ArbitraryNameProvider;
import de.blazemcworld.blazinggames.data.name.UUIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import de.blazemcworld.blazinggames.utils.Pair;
import net.dv8tion.jda.api.entities.User;

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

    @SuppressWarnings("deprecation")
    public DiscordUser updateUser(User user, UUID primary) {
        DiscordUser discordUser = new DiscordUser();
        discordUser.snowflake = user.getIdLong();
        discordUser.displayName = user.getGlobalName();
        discordUser.username = user.getName();
        discordUser.descriminator = user.getDiscriminator();
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

    public void removePlayer(WhitelistedPlayer player) {
        whitelist.deleteData(player.uuid);
    }
    
    public WhitelistedPlayer getWhitelistedPlayer(UUID uuid) {
        return whitelist.getData(uuid);
    }

    public DiscordUser getDiscordUser(long snowflake) {
        return discordUsers.getData(String.valueOf(snowflake));
    }

    public String createLinkCode(String username, UUID user) {
        byte[] bytes = new byte[8];
        new Random().nextBytes(bytes);
        StringBuilder out = new StringBuilder();

        for (byte b : bytes) {
            out.append(String.format("%02x", b));
        }

        linkCodes.put(out.toString(), new Pair<>(username, user));
        return out.toString();
    }

    public Pair<String, UUID> getLinkCodeUser(String code) {
        return linkCodes.getIfPresent(code);
    }

    public void invalidateLinkCode(String code) {
        linkCodes.invalidate(code);
    }
}
