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
package de.blazemcworld.blazinggames.enchantments.sys;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum CustomEnchantmentType {
    NORMAL(NamedTextColor.AQUA, true),
    TWISTED(NamedTextColor.DARK_PURPLE, true),
    COSMETIC(NamedTextColor.DARK_GRAY, true),
    CURSED(NamedTextColor.DARK_RED, false);

    private final TextColor color;
    private final boolean canBeRemoved;

    CustomEnchantmentType(TextColor color, boolean canBeRemoved) {
        this.color = color;
        this.canBeRemoved = canBeRemoved;
    }

    public TextColor getColor() {
        return color;
    }

    public boolean canBeRemoved() {
        return canBeRemoved;
    }
}
