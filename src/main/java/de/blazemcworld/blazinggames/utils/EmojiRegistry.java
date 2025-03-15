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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.blazemcworld.blazinggames.BlazingGames;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class EmojiRegistry {
    public static final String namespace = "blazinggamesemoji";
    public static final String emojiDir = "emojis";
    public static final Pattern pattern = Pattern.compile(":(?<name>[a-z\\-_0-9]{1,50}):");
    public static final HashMap<String, EmojiProperties> emojiMap = new HashMap<>();
    public static final HashMap<String, String> lookupMap = new HashMap<>();
    private EmojiRegistry() {}

    public static void init() {
        reset(); // prevent duplicates
        AtomicInteger index = new AtomicInteger(0xE000); // start of the private use area

        try (
            InputStream stream = EmojiRegistry.class.getClassLoader().getResourceAsStream("emoji.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (line.isBlank() || line.startsWith("#")) continue;

                EmojiProperties props = new EmojiProperties();
                props.character = (char) index.getAndIncrement();

                String[] parts = line.split(" ");
                String emojiName = parts[0];
                
                Arrays.stream(parts).skip(1).forEach(part -> {
                    if (part.contains("=")) {
                        String key = part.substring(0, part.indexOf("="));
                        String value = part.substring(key.length() + 1);

                        switch (key) {
                            case "discord_name" -> props.discordName = value;
                            case "alias" -> props.aliases.add(value);
                            case "attribution" -> props.attribution = value;
                            default -> {
                                BlazingGames.get().log("Unknown parameters " + key + " for emoji " + emojiName);
                            }
                        }
                    }
                });

                emojiMap.put(emojiName, props);
                lookupMap.put(emojiName, emojiName);
                for (String alias : props.aliases) {
                    lookupMap.put(alias, emojiName);
                }
                BlazingGames.get().debugLog("Loaded emoji " + emojiName);
            }
            
        } catch (IOException e) {
            BlazingGames.get().debugLog(e);
            throw new RuntimeException(e);
        }
    }

    public static Component lookup(String name) {
        String realName = lookupMap.getOrDefault(name, null);
        if (realName == null) return null;
        EmojiProperties props = emojiMap.getOrDefault(realName, null);
        if (props == null) return null;
        return render(realName, props);
    }

    public static final TextColor nameColor = TextColor.color(0x9BE8DA);
    public static final TextColor propColor = TextColor.color(0x7C7C7C);
    public static Component render(String name, EmojiProperties props) {
        Component tooltip = Component.text(":" + name + ":", nameColor);

        if (props.aliases.size() > 0) {
            tooltip = tooltip.appendNewline().append(Component.text("Aliases: " + String.join(", ", props.aliases), propColor));
        }

        if (props.attribution != null) {
            tooltip = tooltip.appendNewline().append(Component.text("Attribution: " + props.attribution, propColor));
        }

        return Component.text().content(props.character.toString())
            .font(Key.key(EmojiRegistry.namespace, EmojiRegistry.namespace))
            .color(NamedTextColor.WHITE)
            .decorations(Map.of())
            .hoverEvent(tooltip.asHoverEvent())
            .build();
    }

    public static void reset() {
        emojiMap.clear();
        lookupMap.clear();
    }

    public static Character getChar(String name) {
        EmojiProperties props = emojiMap.getOrDefault(name, null);
        if (props != null) return props.character;
        return null;
    }

    public static Component parseEmoji(Component base) {
        return base.replaceText(b -> b.match(pattern).replacement(builder -> {
            String emojiName = builder.content().substring(1, builder.content().length() - 1);
            if (lookupMap.containsKey(emojiName)) {
                String realName = lookupMap.get(emojiName);
                EmojiProperties props = emojiMap.get(realName);
                return render(realName, props);
            }
            return builder.build();
        }));
    }

    public static String discordParseEmoji(String string) {
        Matcher matcher = pattern.matcher(string);
        String output = string;
        List<String> parsedEmojis = new ArrayList<>();
        while (matcher.find()) {
            String emojiName = matcher.group("name");
            if (parsedEmojis.contains(emojiName)) continue;
            if (lookupMap.containsKey(emojiName)) {
                String realName = lookupMap.get(emojiName);
                EmojiProperties props = emojiMap.get(realName);
                parsedEmojis.add(emojiName);
                if (props.discordName != null) {
                    output = output.replace(matcher.group(), ":" + props.discordName + ":");
                } else {
                    output = output.replace(matcher.group(), "(emoji: " + emojiName + ")");
                }
            }
        }
        return output;
    }

    public static class EmojiProperties {
        public String discordName = null;
        public String attribution = null;
        public List<String> aliases = new ArrayList<>();
        public Character character = null;
    }
}