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
package de.blazemcworld.blazinggames.data.providers;

import de.blazemcworld.blazinggames.data.NameProvider;
import io.azam.ulidj.MonotonicULID;

public class ULIDNameProvider extends NameProvider<String> {
    protected final MonotonicULID ulid = new MonotonicULID();

    @Override
    public String next() {
        return ulid.generate();
    }

    @Override
    public String fromValue(String value) {
        return value;
    }

    @Override
    public String fromString(String string) {
        return string;
    }
}
