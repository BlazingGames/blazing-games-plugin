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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.data.StorageProvider;

public class PropertiesStorageProvider extends StorageProvider<Properties> {
    private final String comments;

    public PropertiesStorageProvider() {
        this.comments = null;
    }

    public PropertiesStorageProvider(String comments) {
        this.comments = comments;
    }

    @Override
    public String fileExtension() {
        return "properties";
    }

    @Override
    public Properties read(byte[] data) {
        Reader reader = new StringReader(new String(data));
        Properties props = new Properties();
        try {
            props.load(reader);
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return null;
        }
        return props;
    }

    @Override
    public byte[] write(Properties data) {
        StringWriter writer = new StringWriter();
        try {
            data.store(writer, comments);
        } catch (IOException e) {
            BlazingGames.get().log(e);
            return null;
        }
        return writer.toString().getBytes();
    }
}