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
package de.blazemcworld.blazinggames.commands.middleware;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelperContext;
import de.blazemcworld.blazinggames.commands.boilerplate.MiddlewareFunction;
import net.kyori.adventure.text.Component;

public class EmptyMessageMiddleware implements MiddlewareFunction {
    @Override
    public boolean apply(CommandHelperContext ctx) throws CommandSyntaxException {
        if (ctx.player() == null) return false;
        ctx.player().sendMessage(Component.empty());
        return true;
    }    
}