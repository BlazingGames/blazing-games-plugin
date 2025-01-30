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
package de.blazemcworld.blazinggames.computing.types;

import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

import de.blazemcworld.blazinggames.computing.types.impl.ConsoleCT;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.utils.AutomaticItemProvider;

public enum ComputerTypes {
    @SerializedName("CONSOLE")
    CONSOLE(new ConsoleCT())
    
    ;

    private final IComputerType type;
    private final Class<? extends IComputerType> clazz;
    private final ComputerItemWrapper item;
    private ComputerTypes(IComputerType type) {
        this.type = type;
        this.clazz = type.getClass();
        this.item = new ComputerItemWrapper(this);
    }

    public IComputerType getType() {
        return this.type;
    }

    public ComputerItemWrapper item() {
        return this.item;
    }

    public static ComputerTypes valueOf(IComputerType computerType) {
        return Arrays.stream(values()).filter(type -> type.clazz.equals(computerType.getClass())).findFirst().orElse(null);
    }

    public static class ComputerTypesProvider implements AutomaticItemProvider.ItemBuilder<ComputerTypes> {
        @Override
        public CustomItem<?> item(ComputerTypes enumValue) {
            return enumValue.item();
        }

        @Override
        public String directoryName() {
            return "computeritems";
        }
    }
}
