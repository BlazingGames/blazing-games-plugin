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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.upgrades.UpgradeType;

public abstract class JSFunctionalClass {
    protected final BootedComputer computer;
    protected final V8Runtime runtime;

    public JSFunctionalClass(BootedComputer computer, V8Runtime runtime) {
        this.computer = computer;
        this.runtime = runtime;
    }

    public abstract String getNamespace();

    protected void actionCooldown(int ticks) {
        int reduction = computer.getMetadata().getUpgradeCount(UpgradeType.ACTION_SPEED);
        int cooldown = (int) Math.ceil(ticks * (1 - (0.1 * reduction))); // reduce by 10% for each level of action speed
        freeze(cooldown);
    }
    protected void freeze(int ticks) {
        if (ticks >= 1) {
            this.computer.freeze(ticks);

            byte[] snapshot;
            try {
                snapshot = this.runtime.createSnapshot();
            } catch (JavetException e) {
                throw new RuntimeException(e);
            }

            this.runtime.terminateExecution();
            if (snapshot != null) {
                this.runtime.getRuntimeOptions().setSnapshotBlob(snapshot);
            }
        }
    }
    protected void stop() { computer.stopCodeExecution(); runtime.terminateExecution(); }
    protected void restart() { computer.restartCodeExecution(); runtime.terminateExecution(); }
}
