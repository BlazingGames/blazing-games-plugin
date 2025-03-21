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

package de.blazemcworld.blazinggames.events.base;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class BlazingEventListener<T extends Event> implements Listener {
    protected List<BlazingEventHandler<T>> handlers = new ArrayList<>();

    public abstract void event(T event);

    public void executeEvent(T event) {
        for (BlazingEventHandler<T> handler : handlers) {
            if (handler.fitCriteria(event, event instanceof Cancellable cancellable && cancellable.isCancelled())) {
                handler.execute(event);
            }
        }
    }
}
