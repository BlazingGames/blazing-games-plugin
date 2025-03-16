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
package de.blazemcworld.blazinggames.commands.finalizers;

import java.util.function.Function;

import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelperContext;
import de.blazemcworld.blazinggames.commands.boilerplate.FinalizerFunction;
import de.blazemcworld.blazinggames.utils.DisplayTag;
import de.blazemcworld.blazinggames.utils.PlayerConfig;
import net.kyori.adventure.text.format.TextColor;

public class ShowNameplatesFinalizer implements FinalizerFunction {
    private final Function<CommandHelperContext, DisplayTag> resolver;
    private final TextColor color;

    public ShowNameplatesFinalizer(final boolean useFront, final TextColor color) {
        this.resolver = context -> PlayerConfig.forPlayer(context.player()).toDisplayTag(useFront);
        this.color = color;
    }

    public ShowNameplatesFinalizer(final String memberArgumentName, final TextColor color) {
        this.resolver = context -> {
            PlayerConfig config = PlayerConfig.forPlayer(context.player());
            return config.getPluralConfig().toDisplayTag(StringArgumentType.getString(context.context(), memberArgumentName), config);
        };
        this.color = color;
    }

    @Override
    public void finalize(CommandHelperContext context) throws CommandSyntaxException {
        Player player = context.player();
        if (player == null) return;

        DisplayTag tag = this.resolver.apply(context);
        tag.sendPreviews(player, color);
    }
}
