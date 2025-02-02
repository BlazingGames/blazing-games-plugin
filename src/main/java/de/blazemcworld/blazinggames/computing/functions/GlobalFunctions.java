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
package de.blazemcworld.blazinggames.computing.functions;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.functions.annotations.MethodDoc;
import de.blazemcworld.blazinggames.computing.functions.annotations.ParamDoc;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GlobalFunctions extends JSFunctionalClass {
    public GlobalFunctions(BootedComputer computer, V8Runtime runtime) {
        super(computer, runtime);
    }

    @Override
    public String getNamespace() {
        return "system";
    }

    @V8Function
    public void debugBroadcast(String message) {
        Bukkit.broadcast(Component.text(message));
    }

    @V8Function
    @MethodDoc("Freezes the computer for the given amount of ticks")
    @ParamDoc(param = "ticks", doc = "The amount of ticks to freeze the computer for")
    public void freeze(int ticks) {
        super.freeze(ticks);
    }

    @V8Function
    @MethodDoc("Stops the computer")
    public void exit() {
        super.stop();
    }

    @V8Function
    @MethodDoc("Restarts the computer")
    public void restart() {
        super.restart();
    }

    @V8Function
    @MethodDoc("Freezes the computer for the given amount of ticks and restarts the computer")
    @ParamDoc(param = "ticks", doc = "The amount of ticks to freeze the computer for")
    public void freezeAndRestart(int ticks) {
        this.computer.restartCodeExecution();
        super.freeze(ticks);
    }
}
