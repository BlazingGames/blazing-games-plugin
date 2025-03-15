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
package de.blazemcworld.blazinggames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.blazemcworld.blazinggames.commands.*;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.computing.ComputerRegistry.ComputerPrivileges;
import de.blazemcworld.blazinggames.computing.api.BlazingAPI;
import de.blazemcworld.blazinggames.computing.api.RequiredFeature;
import de.blazemcworld.blazinggames.discord.AppConfig;
import de.blazemcworld.blazinggames.discord.DiscordApp;
import de.blazemcworld.blazinggames.discord.DiscordNotification;
import de.blazemcworld.blazinggames.events.*;
import de.blazemcworld.blazinggames.items.recipes.CustomRecipes;
import de.blazemcworld.blazinggames.packs.ResourcePackManager;
import de.blazemcworld.blazinggames.packs.ResourcePackManager.PackConfig;
import de.blazemcworld.blazinggames.utils.Cooldown;
import de.blazemcworld.blazinggames.utils.ItemStackTypeAdapter;
import de.blazemcworld.blazinggames.utils.KeyTypeAdapter;
import de.blazemcworld.blazinggames.utils.TextLocation;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.SecretKey;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BlazingGames extends JavaPlugin {
    public boolean API_AVAILABLE = false;

    // Gson
    public static final Gson gson = new GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.PROTECTED, Modifier.TRANSIENT, Modifier.STATIC)
        .registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter())
        .registerTypeAdapter(Location.class, new TextLocation.LocationTypeAdapter())
        .registerTypeAdapter(Key.class, new KeyTypeAdapter())
        .registerTypeAdapter(NamespacedKey.class, new KeyTypeAdapter())
        .create();

    // Cooldowns
    public Cooldown interactCooldown;

    // Logging
    private boolean logErrors = true;
    private boolean logInfo = true;
    private boolean logDebug = false;
    private boolean notifyOpsOnError = true;

    // Computers
    private boolean computersEnabled = false;
    private ComputerPrivileges computerPrivileges = ComputerPrivileges.minimal();

    // Resource pack
    private PackConfig packConfig;
    private File packFile;
    private byte[] sha1;

    @Override
    public void onEnable() {
        // Config
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Log levels
        logErrors = config.getBoolean("logging.log-error");
        logInfo = config.getBoolean("logging.log-info");
        logDebug = config.getBoolean("logging.log-debug");
        notifyOpsOnError = config.getBoolean("logging.notify-ops-on-error");

        // Computers
        computersEnabled = !config.getBoolean("computing.disable-computers");
        if (computersEnabled) {
            computerPrivileges = new ComputerPrivileges(
                    config.getBoolean("computing.privileges.chunkloading"),
                    config.getBoolean("computing.privileges.net")
            );
        }

        // Discord
        if (config.getBoolean("jda.enabled")) {
            AppConfig appConfig = new AppConfig(
                    config.getString("jda.token"),
                    config.getLong("jda.link-channel"),
                    config.getLong("jda.console-channel"),
                    config.getString("jda.webhook"),
                    config.getBoolean("jda.whitelist-management")
            );

            if (config.getBoolean("jda.whitelist-management")) {
                Bukkit.setWhitelist(true);
                Bukkit.setWhitelistEnforced(false);
            }

            try {
                DiscordApp.init(appConfig);
                DiscordApp.send(DiscordNotification.serverStartup());
            } catch (IllegalArgumentException e) {
                getLogger().severe("Failed to start JDA");
                BlazingGames.get().log(e);
            }
        }

        // Recipes
        if (computersEnabled) ComputerRegistry.registerAllRecipes();
        CustomRecipes.loadRecipes();

        // Computers
        if (config.getBoolean("services.blazing-api.enabled") ||
            config.getBoolean("services.blazing-wss.enabled")
        ) {
            log("API or WSS enabled, starting...");

            // Load JWT
            String existingKey = config.getString("authorization.jwt.secret-key");
            boolean isPassword = config.getBoolean("authorization.jwt.secret-key-is-password");
            SecretKey key;
            if (existingKey == null || existingKey.equals("randomize-on-server-start")) {
                SecretKey newKey = SIG.HS256.key().build();
                config.set("authorization.jwt.secret-key", Encoders.BASE64.encode(newKey.getEncoded()));
                config.set("authorization.jwt.secret-key-is-password", false);
                key = newKey;
                saveConfig();
            } else if (isPassword) {
                key = Keys.password(existingKey.toCharArray());
            } else {
                try {
                    key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(existingKey));
                } catch (DecodingException | WeakKeyException e) {
                    log(e);
                    key = null;
                }
            }

            // Microsoft
            boolean spoofMsServer = config.getBoolean("authorization.microsoft.spoof-ms-server");
            String clientId = config.getString("authorization.microsoft.client-id");
            String clientSecret = config.getString("authorization.microsoft.client-secret");

            if (key != null) {
                var apiConfig = BlazingAPI.WebsiteConfig.auto(config, "services.blazing-api");
                var wssConfig = BlazingAPI.WebsiteConfig.auto(config, "services.blazing-wss");

                ArrayList<RequiredFeature> features = new ArrayList<>();
                if (computersEnabled) features.add(RequiredFeature.COMPUTERS);
                if (config.getBoolean("resource-packs.enabled")) features.add(RequiredFeature.RESOURCE_PACK);

                BlazingAPI.setConfig(new BlazingAPI.Config(spoofMsServer, clientId, clientSecret, key, apiConfig, wssConfig, List.copyOf(features)));
                API_AVAILABLE = BlazingAPI.startAll();

                if (API_AVAILABLE) {
                    log("API and/or WSS started");
                } else {
                    getLogger().severe("Failed to start API and/or WSS (see above)");
                }
            } else {
                getLogger().severe("Failed to start API and/or WSS (missing key)");
                API_AVAILABLE = false;
            }
        } else {
            API_AVAILABLE = false;
        }

        // Resource pack
        if (config.getBoolean("resource-packs.enabled") && API_AVAILABLE) {
            this.packConfig = new PackConfig(
                config.getString("resource-packs.metadata.description"),
                UUID.fromString(config.getString("resource-packs.metadata.uuid"))
            );

            rebuildPack();
        } else if (config.getBoolean("resource-packs.enabled") && !API_AVAILABLE) {
            getLogger().severe("The resource pack is enabled, but the API is not available!");
        }

        // Commands
        registerCommand("customenchant", new CustomEnchantCommand());
        registerCommand("customgive", new CustomGiveCommand());
        registerCommand("killme", new KillMeCommand());
        registerCommand("playtime", new PlaytimeCommand());
        registerCommand("display", new DisplayCommand());
        registerCommand("setaltar", new SetAltar());

        if(DiscordApp.isWhitelistManaged()) {
            registerCommand("unlink", new UnlinkCommand());
            registerCommand("discordwhitelist", new DiscordWhitelistCommand());
        }

        // Events
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PrepareAnvilEventListener(), this);
        pluginManager.registerEvents(new PrepareGrindstoneEventListener(), this);
        pluginManager.registerEvents(new ClickInventorySlotEventListener(), this);
        pluginManager.registerEvents(new ChatEventListener(), this);
        pluginManager.registerEvents(new ClickEntityEventListener(), this);
        pluginManager.registerEvents(new BlockBreakEventListener(), this);
        pluginManager.registerEvents(new BlazingBlockDropEventListener(), this);
        pluginManager.registerEvents(new BlazingBlockDisappearEventListener(), this);
        pluginManager.registerEvents(new EntityDeathEventListener(), this);
        pluginManager.registerEvents(new AdvancementEventListener(), this);
        pluginManager.registerEvents(new JoinEventListener(), this);
        pluginManager.registerEvents(new QuitEventListener(), this);
        pluginManager.registerEvents(new InteractEventListener(), this);
        pluginManager.registerEvents(new EntityDamagedByEventListener(), this);
        pluginManager.registerEvents(new BlockPlaceEventListener(), this);
        pluginManager.registerEvents(new SpawnerSpawnEventListener(), this);
        pluginManager.registerEvents(new BlockDestroyEventListener(), this);
        pluginManager.registerEvents(new BlockExplodeEventListener(), this);
        pluginManager.registerEvents(new EntityExplodeEventListener(), this);
        pluginManager.registerEvents(new DeathEventListener(), this);
        pluginManager.registerEvents(new LootGenerateEventListener(), this);
        pluginManager.registerEvents(new PiglinBarterEventListener(), this);
        pluginManager.registerEvents(new VillagerAcquireTradeEventListener(), this);
        pluginManager.registerEvents(new InventoryDragEventListener(), this);
        pluginManager.registerEvents(new InventoryCloseEventListener(), this);
        pluginManager.registerEvents(new PlayerLoginEventListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, TickEventListener::onTick, 0, 1);
        Bukkit.getScheduler().runTaskTimer(this, DiscordWhitelistCommand::enforceWhitelist, 0, 600);

        // Cooldowns
        interactCooldown = new Cooldown(this);
    }

    @Override
    public void onDisable() {
        // Discord
        DiscordApp.send(DiscordNotification.serverShutdown());
        DiscordApp.dispose();

        // Recipes
        CustomRecipes.unloadRecipes();

        // Computers
        BlazingAPI.stopAll();
        API_AVAILABLE = false; // reset value
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = Objects.requireNonNull(getCommand(name));
        command.setExecutor(executor);

        if(executor instanceof TabCompleter tc) {
            command.setTabCompleter(tc);
        }
    }

    public static BlazingGames get()
    {
        return (BlazingGames) BlazingGames.getProvidingPlugin(BlazingGames.class);
    }

    public void log(Object o) {
        if (o instanceof Exception exception) {
            if (logErrors) {
                getLogger().severe("");
                getLogger().severe("An exception occurred:");
                getLogger().severe(exception.getClass().getName());
                getLogger().severe(exception.getMessage());
                StackTraceElement[] stackTrace = exception.getStackTrace();
                for (StackTraceElement element : stackTrace) {
                    getLogger().severe(element.toString());
                }
                getLogger().severe("");
                if (notifyOpsOnError) {
                    Bukkit.broadcast(Component.text(
                        "An exception occurred: " + exception.getMessage() + " (" + exception.getClass().getName()
                        + ") - " + "For more info, see the console."
                    ).color(NamedTextColor.RED), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
                }
            }
        } else {
            if (logInfo) {
                String s = String.valueOf(o);
                getLogger().info(s);
            }
        }
    }

    public void debugLog(Object o) {
        if (logDebug) {
            log(o);
        }
    }

    public NamespacedKey key(String id) {
        return new NamespacedKey(this, id);
    }

    public boolean areComputersEnabled() {
        return computersEnabled;
    }

    public ComputerPrivileges getComputerPrivileges() {
        return computerPrivileges;
    }

    public boolean isApiAvailable() {
        return API_AVAILABLE;
    }

    public PackConfig getPackConfig() {
        return packConfig;
    }

    public File getPackFile() {
        return packFile;
    }

    public byte[] getPackSha1() {
        return sha1;
    }

    public void rebuildPack() {
        var file = ResourcePackManager.build(getLogger(), packConfig);
        if (file != null) {
            this.packFile = file;
            this.sha1 = ResourcePackManager.getFileHash(packFile);
            getLogger().info("Resource pack rebuilt");
        }
    }
}
