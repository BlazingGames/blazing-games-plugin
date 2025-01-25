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

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Drops extends ArrayList<ItemStack> {
    private int expToGive = 0;

    public Drops() {
        super();
    }

    public Drops(Collection<ItemStack> drops) {
        this();
        this.addAll(drops);
    }

    public Drops(ItemStack... drops) {
        this(List.of(drops));
    }

    public int getExperienceDropped() {
        return expToGive;
    }

    public void addExperience(int expAmount) {
        expToGive += expAmount;
    }

    public void setExperience(int expAmount) {
        expToGive = expAmount;
    }
}
