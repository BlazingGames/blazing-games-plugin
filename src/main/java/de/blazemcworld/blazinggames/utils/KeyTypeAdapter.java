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

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.kyori.adventure.key.Key;

public class KeyTypeAdapter extends TypeAdapter<Key> {

    @Override
    public void write(JsonWriter out, Key value) throws IOException {
        if (value == null) { out.nullValue(); return; }
        out.value(value.asString());
    }

    @Override
    public Key read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.STRING) return null;
        String value = in.nextString();
        return Key.key(value);
    }
}
