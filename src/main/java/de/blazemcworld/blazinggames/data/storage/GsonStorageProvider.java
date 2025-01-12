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
package de.blazemcworld.blazinggames.data.storage;

import java.lang.reflect.Type;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.data.StorageProvider;

public class GsonStorageProvider<T> extends StorageProvider<T> {
    public GsonStorageProvider(Type type) {
        this.type = type;
    }
    protected final Type type;

    @Override
    public String fileExtension() {
        return "json";
    }

    @Override
    public T read(byte[] data) {
        return BlazingGames.gson.fromJson(new String(data), type);
    }

    @Override
    public byte[] write(T data) {
        return BlazingGames.gson.toJson(data, type).getBytes();
    }
}
