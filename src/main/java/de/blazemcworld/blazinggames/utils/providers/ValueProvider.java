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

package de.blazemcworld.blazinggames.utils.providers;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface ValueProvider<T extends Keyed> {
    default List<? extends T> list() {
        return List.of();
    }

    default Set<? extends T> set() {
        return list().stream().collect(Collectors.toUnmodifiableSet());
    }

    default Map<NamespacedKey, ? extends T> map() {
        ImmutableMap.Builder<NamespacedKey, T> map = new ImmutableMap.Builder<>();
        for(T value : list()) {
            map.put(value.getKey(), value);
        }
        return map.build();
    }

    default @Nullable T getByKey(NamespacedKey key) {
        for(T value : list()) {
            if(value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }
}
