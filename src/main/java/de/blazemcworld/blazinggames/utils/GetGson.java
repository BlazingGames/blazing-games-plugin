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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetGson {
    private GetGson() {
    }

    private static <T extends Throwable> JsonPrimitive _getAsPrimitive(JsonElement element, T t) throws T {
        if (element == null) {
            throw t;
        } else if (!element.isJsonPrimitive()) {
            throw t;
        } else {
            return element.getAsJsonPrimitive();
        }
    }

    public static <T extends Throwable> String getString(JsonObject object, String key, T t) throws T {
        if (object == null) {
            throw t;
        } else if (!object.has(key)) {
            throw t;
        } else {
            return getAsString(object.get(key), t);
        }
    }

    public static <T extends Throwable> String getAsString(JsonElement element, T t) throws T {
        JsonPrimitive primitive = _getAsPrimitive(element, t);
        if (!primitive.isString()) {
            throw t;
        } else {
            return primitive.getAsString();
        }
    }

    public static <T extends Throwable> Number getNumber(JsonObject object, String key, T t) throws T {
        if (object == null) {
            throw t;
        } else if (!object.has(key)) {
            throw t;
        } else {
            return getAsNumber(object.get(key), t);
        }
    }

    public static <T extends Throwable> Number getAsNumber(JsonElement element, T t) throws T {
        JsonPrimitive primitive = _getAsPrimitive(element, t);
        if (!primitive.isNumber()) {
            throw t;
        } else {
            return primitive.getAsNumber();
        }
    }

    public static <T extends Throwable> Boolean getBoolean(JsonObject object, String key, T t) throws T {
        if (object == null) {
            throw t;
        } else if (!object.has(key)) {
            throw t;
        } else {
            return getAsBoolean(object.get(key), t);
        }
    }

    public static <T extends Throwable> Boolean getAsBoolean(JsonElement element, T t) throws T {
        JsonPrimitive primitive = _getAsPrimitive(element, t);
        if (!primitive.isBoolean()) {
            throw t;
        } else {
            return primitive.getAsBoolean();
        }
    }

    public static <T extends Throwable> JsonObject getObject(JsonObject object, String key, T t) throws T {
        if (object == null) {
            throw t;
        } else if (!object.has(key)) {
            throw t;
        } else {
            return getAsObject(object.get(key), t);
        }
    }

    public static <T extends Throwable> JsonObject getAsObject(JsonElement element, T t) throws T {
        if (element == null) {
            throw t;
        } else if (!element.isJsonObject()) {
            throw t;
        } else {
            return element.getAsJsonObject();
        }
    }

    public static <T extends Throwable> JsonArray getArray(JsonObject object, String key, T t) throws T {
        if (object == null) {
            throw t;
        } else if (!object.has(key)) {
            throw t;
        } else {
            return getAsArray(object.get(key), t);
        }
    }

    public static <T extends Throwable> JsonArray getAsArray(JsonElement element, T t) throws T {
        if (element == null) {
            throw t;
        } else if (!element.isJsonArray()) {
            throw t;
        } else {
            return element.getAsJsonArray();
        }
    }
}
