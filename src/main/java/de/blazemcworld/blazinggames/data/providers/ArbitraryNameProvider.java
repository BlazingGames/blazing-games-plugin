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

import java.util.function.Supplier;

import de.blazemcworld.blazinggames.data.NameProvider;

public class ArbitraryNameProvider extends NameProvider<String> {
    protected final Supplier<String> supplier;


    public ArbitraryNameProvider() {
        this.supplier = () -> null;
    }

    public ArbitraryNameProvider(final String value) {
        this.supplier = () -> value;
    }

    public ArbitraryNameProvider(final Supplier<String> supplier) {
        this.supplier = supplier;
    }

    @Override
    public String next() {
        return supplier.get();
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
