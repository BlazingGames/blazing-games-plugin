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

import java.util.Properties;
import java.util.UUID;

import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.UUIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.PropertiesStorageProvider;
import net.kyori.adventure.text.format.TextColor;

public class PlayerConfig {
    private static final DataStorage<Properties, UUID> dataStorage = DataStorage.forClass(
        PlayerConfig.class, null,
        new PropertiesStorageProvider(), new UUIDNameProvider(), new GZipCompressionProvider()
    );

    public static PlayerConfig forPlayer(UUID uuid) {
        if (dataStorage.getData(uuid) == null) dataStorage.storeData(uuid, new Properties());
        return new PlayerConfig(dataStorage.getData(uuid), uuid);
    }

    private final Properties props;
    private final UUID uuid;
    private PlayerConfig(Properties props, UUID uuid) {
        this.props = props;
        this.uuid = uuid;
    }

    private void write() {
        dataStorage.storeData(uuid, props);
    }



    public String getDisplayName() {
        String value = props.getProperty("displayname", null);
        if (value == null || value.isBlank()) return null;
        return value;
    }

    public void setDisplayName(String name) {
        if (name == null || name.isBlank()) props.remove("displayname");
        else props.setProperty("displayname", name);
        write();
    }



    public String getPronouns() {
        String value = props.getProperty("pronouns", null);
        if (value == null || value.isBlank()) return null;
        return value;
    }

    public void setPronouns(String pronouns) {
        if (pronouns == null || pronouns.isBlank()) props.remove("pronouns");
        else props.setProperty("pronouns", pronouns);
        write();
    }



    public TextColor getNameColor() {
        return TextColor.color(Integer.valueOf(props.getProperty("namecolor", "16777215")));
    }

    public void setNameColor(TextColor color) {
        if (color == null) props.remove("namecolor");
        else props.setProperty("namecolor", String.valueOf(color.value()));
        write();
    }
}
