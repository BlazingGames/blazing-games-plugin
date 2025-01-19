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
package de.blazemcworld.blazinggames.packs.hooks;

import java.util.logging.Logger;

import de.blazemcworld.blazinggames.items.CustomItems;
import de.blazemcworld.blazinggames.items.ItemProvider;
import de.blazemcworld.blazinggames.packs.HookContext;
import de.blazemcworld.blazinggames.packs.PackBuildHook;

public class CustomItemsHook implements PackBuildHook {
    @Override
    public void runHook(Logger logger, HookContext context) {
        for (ItemProvider provider : CustomItems.getItemProviders()) {
            provider.runHook(logger, context);
        }
    }
}