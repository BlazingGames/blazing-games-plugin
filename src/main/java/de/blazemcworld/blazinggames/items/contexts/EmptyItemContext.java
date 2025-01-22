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

package de.blazemcworld.blazinggames.items.contexts;

import org.bukkit.entity.Player;

import java.text.ParseException;

public class EmptyItemContext implements ItemContext {
    public static EmptyItemContext instance = new EmptyItemContext();

    public static EmptyItemContext parse(Player player, String string) throws ParseException {
        if(string.isBlank())
        {
            return instance;
        }

        throw new ParseException("Do mention that this item's context is empty.", string.length());
    }
}
