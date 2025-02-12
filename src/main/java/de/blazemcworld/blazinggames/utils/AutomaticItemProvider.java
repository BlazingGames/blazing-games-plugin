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

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.ItemProvider;
import de.blazemcworld.blazinggames.packs.HookContext;

public class AutomaticItemProvider implements ItemProvider {
    private final String directoryName;
    private final Set<CustomItem<?>> items;
    public <T extends Enum<?>> AutomaticItemProvider(T[] values, ItemBuilder<T> provider) {
        this.directoryName = provider.directoryName();
        this.items = Arrays.stream(values).map(i -> provider.item(i)).collect(Collectors.toSet());
    }

    @Override
    public Set<CustomItem<?>> getItems() {
        return items;
    }

    public static interface ItemBuilder<T extends Enum<?>> {
        String directoryName();
        CustomItem<?> item(T enumValue);
    }

    @Override
    public void runHook(Logger logger, HookContext context) {
        ItemProvider.installItems(directoryName, this, logger, context);
    }
}