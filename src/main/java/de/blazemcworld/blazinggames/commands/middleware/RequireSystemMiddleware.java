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

import org.bukkit.entity.Player;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelperContext;
import de.blazemcworld.blazinggames.commands.boilerplate.MiddlewareFunction;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class RequireSystemMiddleware implements MiddlewareFunction {
    private final TextColor color;
    public RequireSystemMiddleware(TextColor color) {
        this.color = color;
    }
    @Override
    public boolean apply(CommandHelperContext ctx) throws CommandSyntaxException {
        Player player = ctx.player();
        if (player == null) {
            return false;
        }
        boolean result = PlayerConfig.forPlayer(player).isPlural();
        if (!result) {
            player.sendMessage(Component.text((
                "Your account must be marked as a system to use this command. (run /system enable)"
            ), color));
        }
        return result;
    }
}