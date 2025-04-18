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

package de.blazemcworld.blazinggames.computing.eventhandlers;

import de.blazemcworld.blazinggames.computing.BootedComputer;
import de.blazemcworld.blazinggames.computing.ComputerRegistry;
import de.blazemcworld.blazinggames.events.BlazingBlockDropEvent;
import de.blazemcworld.blazinggames.events.base.BlazingEventHandler;

public class ComputerDropHandler extends BlazingEventHandler<BlazingBlockDropEvent> {
    @Override
    public boolean fitCriteria(BlazingBlockDropEvent event, boolean cancelled) {
        return ComputerRegistry.getComputerByLocationRounded(event.getBlock().getLocation()) != null;
    }

    @Override
    public void execute(BlazingBlockDropEvent event) {
        BootedComputer computer = ComputerRegistry.getComputerByLocationRounded(event.getBlock().getLocation());
        event.getDrops().clear();
        event.getDrops().setExperience(0);
        event.getDrops().add(ComputerRegistry.addAttributes(computer.getType().getType().getDisplayItem(computer), computer));
    }
}
