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
package de.blazemcworld.blazinggames.players;

import net.kyori.adventure.text.format.TextColor;

public interface DisplayConfigurationEditor {
    String getDisplayName();
    void setDisplayName(String name);

    String getPronouns();
    void setPronouns(String pronouns);

    TextColor getNameColor();
    void setNameColor(TextColor color);

    default void reset() {
        setDisplayName(null);
        setPronouns(null);
        setNameColor(null);
    }

    default void setProperty(DisplayTagProperty type, String value) throws IllegalArgumentException {
        if (!ServerPlayerConfig.isLengthValid(value) && type.lengthCheck) {
            throw new IllegalArgumentException("Length must be between " + ServerPlayerConfig.minLength() + " and " + ServerPlayerConfig.maxLength() + " characters long.");
        }

        switch (type) {
            case DISPLAY_NAME:
                setDisplayName(value);
                break;
            case PRONOUNS:
                setPronouns(value);
                break;
            case NAME_COLOR:
                if (value == null) {
                    setNameColor(null);
                    return;
                }
                if (value.length() != 6) {
                    throw new IllegalArgumentException("Colors must be a hex color, without a #. For example, \"ffffff\".");
                }
                int intValue;
                try {
                    intValue = Integer.parseInt(value, 16);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Not a valid hex color: #" + value);
                }
                setNameColor(TextColor.color(intValue));
                break;
        }
    }
}