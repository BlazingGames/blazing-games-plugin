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
package de.blazemcworld.blazinggames.commands.boilerplate;

import java.util.ArrayList;
import java.util.List;

public class CommandHelperBuilder {
    private boolean ignoreExecutor = false;
    private final ArrayList<MiddlewareFunction> middleware = new ArrayList<>();
    private final ArrayList<FinalizerFunction> finalizers = new ArrayList<>();

    public CommandHelperBuilder ignoreExecutor(boolean ignoreExecutor) {
        this.ignoreExecutor = ignoreExecutor;
        return this;
    }

    public CommandHelperBuilder middleware(MiddlewareFunction middleware) {
        this.middleware.add(middleware);
        return this;
    }

    public CommandHelperBuilder finalizer(FinalizerFunction finalizer) {
        this.finalizers.add(finalizer);
        return this;
    }

    public CommandHelper build() {
        return new CommandHelper(
            ignoreExecutor,
            List.copyOf(middleware),
            List.copyOf(finalizers)
        );
    }
}