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

import java.io.IOException;
import java.nio.file.FileSystem;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.packs.ResourcePackManager.PackConfig;

public class HookContext {
    public final FileSystem fileSystem;
    public final PackConfig config;
    public HookContext(FileSystem fileSystem, PackConfig config) {
        this.fileSystem = fileSystem;
        this.config = config;
    }

    public void writeFile(String path, byte[] contents) {
        String realPath = path.startsWith("/") ? path : "/" + path;
        try {
            ResourcePackManager.write(fileSystem.getPath(realPath), contents);
        } catch (IOException e) {
            BlazingGames.get().log(e);
        }
    }

    public void writeFile(String path, String contents) {
        writeFile(path, contents.getBytes());
    }

    public void writeFile(String path, JsonObject contents) {
        writeFile(path, contents.toString().getBytes());
    }
}