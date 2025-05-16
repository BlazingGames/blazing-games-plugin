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
package de.blazemcworld.blazinggames.utils.adapters;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.World;

/**
 * Location represented as a string
 */
public class TextLocation {
    private TextLocation() {
    }

    public static String serialize(Location location) {
        return location == null ? null
            : location.getWorld().getName()
                + " "
                + location.getX()
                + " "
                + location.getY()
                + " "
                + location.getZ()
                + " "
                + location.getYaw()
                + " "
                + location.getPitch();
    }

    public static String serializeRounded(Location location) {
        return location == null ? null
            : location.getWorld().getName()
                + " "
                + location.blockX()
                + " "
                + location.blockY()
                + " "
                + location.blockZ()
                + " 0.0 0.0";
    }

    public static Location deserialize(String serialized) {
        if (serialized != null && !serialized.isEmpty()) {
            String[] split = serialized.split(" ");
            String worldName = split[0];
            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            float yaw = Float.parseFloat(split[4]);
            float pitch = Float.parseFloat(split[5]);
            return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        } else {
            return null;
        }
    }

    public static Location deserializeUserInput(World world, String serialized) {
        if (serialized != null && !serialized.isEmpty()) {
            String[] split = serialized.split(" ");
            switch (split.length) {
                case 6: { // world, x, y, z, yaw, pitch
                    String worldName = split[0];
                    double x = Double.parseDouble(split[1]);
                    double y = Double.parseDouble(split[2]);
                    double z = Double.parseDouble(split[3]);
                    float yaw = Float.parseFloat(split[4]);
                    float pitch = Float.parseFloat(split[5]);
                    return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
                }
                case 4: { // world, x, y, z
                    String worldName = split[0];
                    double x = Double.parseDouble(split[1]);
                    double y = Double.parseDouble(split[2]);
                    double z = Double.parseDouble(split[3]);
                    return new Location(Bukkit.getWorld(worldName), x, y, z);
                }
                case 3: { // x, y, z
                    double x = Double.parseDouble(split[0]);
                    double y = Double.parseDouble(split[1]);
                    double z = Double.parseDouble(split[2]);
                    return new Location(world, x, y, z);
                }
                default: {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static class LocationTypeAdapter extends TypeAdapter<Location> {
        @Override
        public void write(JsonWriter out, Location value) throws IOException {
            out.value(TextLocation.serialize(value));
        }

        @Override
        public Location read(JsonReader in) throws IOException {
            if (in.peek() != JsonToken.STRING) return null;
            String value = in.nextString();
            return TextLocation.deserialize(value);
        }
    }
}
