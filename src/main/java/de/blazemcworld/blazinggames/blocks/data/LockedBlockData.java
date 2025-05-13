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

package de.blazemcworld.blazinggames.blocks.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.blocks.wrappers.BlockWrapper;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.ItemProviders;
import de.blazemcworld.blazinggames.items.predicates.ItemPredicate;
import org.bukkit.NamespacedKey;

public class LockedBlockData extends CustomBlockData {
    BlockWrapper wrapper;
    CustomItem<?> key;

    public LockedBlockData(BlockWrapper wrapper, CustomItem<?> key) {
        this.wrapper = wrapper.clone();
        this.key = key;
    }

    public BlockWrapper getWrapper() {
        return wrapper;
    }

    public ItemPredicate getKeyPredicate() {
        return key;
    }

    @Override
    public LockedBlockData clone() {
        return new LockedBlockData(wrapper, key);
    }

    @Override
    public JsonObject serialize() {
        JsonObject obj = new JsonObject();

        obj.add("wrapper", BlazingGames.gson.toJsonTree(wrapper, BlockWrapper.class));
        obj.add("key", BlazingGames.gson.toJsonTree(key.getKey(), NamespacedKey.class));

        return obj;
    }

    public static LockedBlockData deserialize(JsonObject data) {
        NamespacedKey keyKey = BlazingGames.gson.fromJson(data.get("key"), NamespacedKey.class);

        CustomItem<?> key = ItemProviders.instance.getByKey(keyKey);

        if(key == null) {
            throw new JsonParseException("Could not find a custom item type with id " + keyKey.toString());
        }

        BlockWrapper wrapper = BlazingGames.gson.fromJson(data.get("wrapper"), BlockWrapper.class);

        return new LockedBlockData(wrapper, key);
    }
}
