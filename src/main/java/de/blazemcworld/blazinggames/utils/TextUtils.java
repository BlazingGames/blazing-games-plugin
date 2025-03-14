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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class TextUtils {
    public static final MiniMessage restrictedParser = MiniMessage.builder()
        .tags(TagResolver.builder()
            .resolver(StandardTags.color())
            .resolver(StandardTags.decorations())
            .resolver(StandardTags.gradient())
            .resolver(StandardTags.shadowColor())
            .resolver(StandardTags.pride())
            .resolver(StandardTags.rainbow())
            .resolver(StandardTags.keybind())
            .resolver(StandardTags.reset())
            .resolver(StandardTags.newline())
        .build()).build();

    public static String componentToString(Component component) {
        return restrictedParser.serialize(component);
    }

    public static String stripStyles(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static Component stringToComponent(String string) {
        return Component.text(string);
    }

    public static Component parseMinimessage(String message) {
        return restrictedParser.deserialize(message);
    }
}
