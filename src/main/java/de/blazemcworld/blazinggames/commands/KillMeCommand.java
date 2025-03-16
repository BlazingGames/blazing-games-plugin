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

import de.blazemcworld.blazinggames.BlazingGames;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class KillMeCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("killme")
            .executes(ctx -> {
                if (ctx.getSource().getExecutor() == null || !(ctx.getSource().getExecutor() instanceof final Player player)) {
                    ctx.getSource().getSender().sendRichMessage("<red>This command is only for players!");
                    return Command.SINGLE_SUCCESS;
                }

                BukkitRunnable run = new BukkitRunnable() {
                    int damage = 1;
                    int damageTick = 0;
        
                    @Override
                    public void run() {
                        player.damage(damage);
                        damageTick++;
                        if (damageTick >= 10) {
                            damageTick = 0;
                            damage++;
                        }
                        if (!player.isValid() || player.isDead()) {
                            cancel();
                        }
                    }
                };
        
                run.runTaskTimer(BlazingGames.get(), 0, 10);
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }
}