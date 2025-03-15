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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextUtils {
    public static String componentToString(Component component) {
        if (component == null) return null;
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static String componentToAmpersandString(Component component) {
        if (component == null) return null;
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static Component stringToComponent(String string) {
        if (string == null) return null;
        return Component.text(string);
    }

    public static Component ampersandStringToComponent(String string) {
        if (string == null) return null;
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }

    public static Component colorCodeParser(Component message) {
        if (message == null) return null;
        String text = componentToString(message)
                .replaceAll("&([a-fk-or0-9])", "§$1");
        return Component.text(text);
    }

    public static String stripColorCodes(String message) {
        if (message == null) return null;
        return message.replaceAll("&[0-9a-fk-or]", "");
    }
}
