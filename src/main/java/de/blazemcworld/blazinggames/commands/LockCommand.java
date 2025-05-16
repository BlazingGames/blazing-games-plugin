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
package de.blazemcworld.blazinggames.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.blazemcworld.blazinggames.BlazingGames;
import de.blazemcworld.blazinggames.blocks.LockedBlock;
import de.blazemcworld.blazinggames.commands.boilerplate.CommandHelper;
import de.blazemcworld.blazinggames.items.CustomItem;
import de.blazemcworld.blazinggames.items.ItemProviders;
import de.blazemcworld.blazinggames.packs.hooks.LockedBlockStylesHook;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;

public class LockCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("lock")
            .requires(ctx -> ctx.getSender().isOp() || ctx.getSender().hasPermission("blazinggames.lock"))
            .then(Commands.argument("position", ArgumentTypes.blockPosition())
                .then(Commands.argument("keyItem", StringArgumentType.word())
                    .then(Commands.argument("style", StringArgumentType.word())
                        .then(Commands.argument("color", ArgumentTypes.namedColor())
                            .executes(CommandHelper.getDefault().wrap(LockCommand::handle))
                        )
                    )
                )
            ).build();
    }

    public static void handle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final String id = ctx.getArgument("keyItem", String.class);

        CustomItem<?> itemType = ItemProviders.instance.getByKey(BlazingGames.get().key(id));

        if(itemType == null) {
            ctx.getSource().getSender().sendMessage(Component.text("Unknown custom item: " + id + "!").color(NamedTextColor.RED));
            return;
        }

        final String styleId = ctx.getArgument("style", String.class);

        LockedBlockStylesHook.LockedBlockStyle style = LockedBlockStylesHook.LockedBlockStyle.getByKey(BlazingGames.get().key(styleId));

        if(style == null) {
            ctx.getSource().getSender().sendMessage(Component.text("Unknown locked block style: " + styleId + "!").color(NamedTextColor.RED));
            return;
        }

        TextColor argColor = ctx.getArgument("color", NamedTextColor.class);

        Color color = Color.fromRGB(argColor.red(), argColor.green(), argColor.blue());

        final BlockPositionResolver resolver = ctx.getArgument("position", BlockPositionResolver.class);
        final BlockPosition blockPosition = resolver.resolve(ctx.getSource());

        Location location = blockPosition.toLocation(ctx.getSource().getLocation().getWorld());

        String locationString = blockPosition.x() + ", "
                + blockPosition.y() + ", "
                + blockPosition.z();

        if(!LockedBlock.lock(location, itemType, style, color)) {
            ctx.getSource().getSender().sendRichMessage("<red>Unable to lock block at " + locationString);
            return;
        }

        ctx.getSource().getSender().sendRichMessage("<green>Successfully locked the block at " + locationString);
    }
}
