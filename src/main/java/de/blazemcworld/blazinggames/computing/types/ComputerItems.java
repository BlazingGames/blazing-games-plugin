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
package de.blazemcworld.blazinggames.computing.types;

import java.util.Arrays;
import java.util.Set;

import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.ItemProvider;

public class ComputerItems implements ItemProvider {
    @Override
    public Set<CustomItem<?>> getItems() {
        return Arrays.stream(ComputerTypes.values()).map(ComputerTypes::item).collect(java.util.stream.Collectors.toSet());
    }
}
