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
package de.blazemcworld.blazinggames.packs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import io.azam.ulidj.MonotonicULID;

public class ResourcePackManager {
    private static final MonotonicULID ulid = new MonotonicULID();
    private static final File workingDirectory = new File(".packwork");
    static {
        workingDirectory.mkdirs();
    }

    public static record PackConfig(
        String description,
        UUID uuid
    ) {}

    public static File build(Logger log, PackConfig config) {
        File outputFile = new File(workingDirectory, ulid.generate() + ".zip");
        Map<String, String> environment = new HashMap<>();
        environment.put("create", "true");
        URI uri = URI.create("jar:file:" + outputFile.getAbsolutePath());

        log.info("Building resource pack...");

        try (
            FileSystem zip = FileSystems.newFileSystem(uri, environment);
        ) {
            // create pack meta
            JsonObject root = new JsonObject();
            JsonObject meta = new JsonObject();
            meta.addProperty("pack_format", 61);
            meta.addProperty("description", config.description);
            root.add("pack", meta);
            write(zip.getPath("/pack.mcmeta"), root.toString().getBytes());
            log.info("Wrote pack metadata");

            // run hooks
            HookContext context = new HookContext(zip, config);
            for (HookList hook : HookList.values()) {
                log.info("Running hook " + hook.hook.getClass().getSimpleName());
                hook.hook.runHook(log, context);
                log.info("Finished hook " + hook.hook.getClass().getSimpleName());
            }
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return null;
        }

        return outputFile;
    }

    public static void write(Path path, byte[] data) throws IOException {
        Files.createDirectories(path.getParent());
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            outputStream.write(data);
        }
    }

    public static byte[] getFileHash(File file) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            BlazingGames.get().log(e);
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return digest.digest(fileInputStream.readAllBytes());
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return null;
        }
    }
}