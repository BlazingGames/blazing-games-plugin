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
import java.io.StringReader;
import java.util.Base64;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {
    private static Function<String, String> decompress = s -> GZipToolkit.decompress(Base64.getDecoder().decode(s));
    private static Function<String, String> compress = s -> Base64.getEncoder().encodeToString(GZipToolkit.compress(s));

    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        if (value == null) { out.nullValue(); return; }
        out.beginObject();
        out.name("material").value(value.getType().name());
        out.name("amount").value(value.getAmount());
        if (value.hasItemMeta()) {
            FileConfiguration metadata = new YamlConfiguration();
            metadata.createSection("data", value.getItemMeta().serialize());
            metadata.getConfigurationSection("data").set("==", ConfigurationSerialization.getAlias(value.getItemMeta().getClass()));
            String metadataString = compress.apply(metadata.saveToString());
            out.name("metadata").value(metadataString);
        }
        out.endObject();
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
        
        Material material = null;
        Integer amount = null;
        String metadataString = null;
        
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("material")) {
                material = Material.valueOf(in.nextString());
            } else if (name.equals("amount")) {
                amount = in.nextInt();
            } else if (name.equals("metadata")) {
                metadataString = in.nextString();
            }
        }
        in.endObject();

        if (material == null || amount == null) {
            throw new IOException("Could not deserialize: material or amount is null");
        }

        ItemStack item = new ItemStack(material, amount);

        if (metadataString != null) {
            FileConfiguration metadata = YamlConfiguration.loadConfiguration(new StringReader(decompress.apply(metadataString)));
            item.setItemMeta((ItemMeta) metadata.get("data"));
        }
        
        return item;
    }
}
