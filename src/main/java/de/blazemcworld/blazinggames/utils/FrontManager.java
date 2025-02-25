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

import java.util.HashMap;
import java.util.UUID;

public class FrontManager {
    private FrontManager() {}
    private static final HashMap<UUID, String> frontMap = new HashMap<>();

    public static void updateFront(UUID uuid, String front) {
        frontMap.put(uuid, front);
    }

    public static String getFront(UUID uuid) {
        return frontMap.get(uuid);
    }

    public static void clearFront(UUID uuid) {
        frontMap.remove(uuid);
    }

    public static boolean hasFront(UUID uuid) {
        return frontMap.containsKey(uuid);
    }
}