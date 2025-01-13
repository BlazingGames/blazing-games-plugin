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
package de.blazemcworld.blazinggames.computing.api;

import java.util.function.Function;

import com.google.gson.JsonObject;

import de.blazemcworld.blazinggames.utils.GetGson;

public class BodyWrapper<T extends Throwable> {
    public final JsonObject body;
    public final Function<String, T> exceptionSupplier;

    public BodyWrapper(JsonObject body, Function<String, T> exceptionSupplier) {
        this.body = body;
        this.exceptionSupplier = exceptionSupplier;
    }

    public String getString(String key) throws T {
        return GetGson.getString(body, key, exceptionSupplier.apply(key));
    }

    public Number getNumber(String key) throws T {
        return GetGson.getNumber(body, key, exceptionSupplier.apply(key));
    }

    public Boolean getBoolean(String key) throws T {
        return GetGson.getBoolean(body, key, exceptionSupplier.apply(key));
    }

    public BodyWrapper<T> getObject(String key) throws T {
        return new BodyWrapper<>(GetGson.getObject(body, key, exceptionSupplier.apply(key)), exceptionSupplier);
    }
    
    public boolean hasValue(String key) {
        return body.has(key);
    }
}