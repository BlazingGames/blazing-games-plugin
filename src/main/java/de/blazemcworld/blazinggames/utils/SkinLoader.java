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
package de.blazemcworld.blazinggames.utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.mineskin.MineSkinClient;
import org.mineskin.data.TextureInfo;

import com.destroystokyo.paper.profile.ProfileProperty;

import de.blazemcworld.blazinggames.BlazingGames;
import dev.ivycollective.datastorage.DataStorage;
import dev.ivycollective.datastorage.name.UUIDNameProvider;
import dev.ivycollective.datastorage.storage.GsonStorageProvider;

public class SkinLoader {
    private static MineSkinClient client = null;
    
    private static final DataStorage<ProfileProperty, UUID> cache = BlazingGames.dataStorageConfig().makeDataStorage(
        SkinLoader.class, "cache",
        new GsonStorageProvider<ProfileProperty>(ProfileProperty.class),
        new UUIDNameProvider()
    );

    public static CompletableFuture<ProfileProperty> getProfile(final UUID uuid) {
        if (client == null) return CompletableFuture.failedFuture(new IllegalStateException("SkinLoader not initialized"));

        if (cache.hasData(uuid)) {
            return CompletableFuture.completedFuture(cache.getData(uuid));
        } else {
            final CompletableFuture<ProfileProperty> future = new CompletableFuture<>();
            client.skins().get(uuid)
                .thenAccept(skin -> {
                    Bukkit.getScheduler().runTask(BlazingGames.get(), () -> {
                        TextureInfo textureInfo = skin.getSkin().texture();
                        ProfileProperty prop = new ProfileProperty("textures", textureInfo.data().value(), textureInfo.data().signature());
                        SkinRenderer.updateMineskin(uuid, prop);
                        future.complete(prop);
                    });
                })
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });
            return future;
        }
    }

    public static void init(MineSkinClient client) {
        SkinLoader.client = client;
    }

    public static void shutdown() {
        SkinLoader.client = null;
    }
}
