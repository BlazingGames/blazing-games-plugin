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
import java.util.function.Supplier;

import com.google.gson.annotations.SerializedName;

public enum ComputerTypes {
    @SerializedName("CONSOLE")
    CONSOLE(ConsoleCT::new, ConsoleCT.class);

    private final Supplier<IComputerType> type;
    private final Class<? extends IComputerType> clazz;

    private ComputerTypes(Supplier<IComputerType> type, Class<? extends IComputerType> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public IComputerType getType() {
        return this.type.get();
    }

    public static ComputerTypes valueOf(IComputerType computerType) {
        return Arrays.stream(values()).filter(type -> type.clazz.equals(computerType.getClass())).findFirst().orElse(null);
    }
}
