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

public class ServerPlayerConfig {
    public static boolean relaxed = false;

    public static int minLength() {
        if (relaxed) return 1;
        else return 3;
    }

    public static int maxLength() {
        if (relaxed) return 240;
        else return 80;
    }

    public static boolean isLengthValid(String checkStr) {
        return checkStr.length() >= minLength() && checkStr.length() <= maxLength();
    }

    //

    public static void reset() {
        relaxed = false;
    }
}
