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

import java.io.IOException;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ProfilePropertyTypeAdapter extends TypeAdapter<ProfileProperty> {
    @Override
    public void write(JsonWriter out, ProfileProperty value) throws IOException {
        if (value == null) { out.nullValue(); return; }
        out.beginObject();
        out.name("name").value(value.getName());
        out.name("value").value(value.getValue());
        out.name("signature").value(value.getSignature());
        out.endObject();
    }

    @Override
    public ProfileProperty read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) return null;
        in.beginObject();
        String name = null;
        String value = null;
        String signature = null;
        while (in.hasNext()) {
            String prop = in.nextName();
            if (prop.equals("name")) {
                name = in.nextString();
            } else if (prop.equals("value")) {
                value = in.nextString();
            } else if (prop.equals("signature")) {
                signature = in.nextString();
            }
        }
        in.endObject();
        return new ProfileProperty(name, value, signature);
    }
}
