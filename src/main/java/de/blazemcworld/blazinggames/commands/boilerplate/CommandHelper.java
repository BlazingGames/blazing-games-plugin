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

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;

public record CommandHelper(
    boolean ignoreExecutor,
    List<MiddlewareFunction> middleware,
    List<FinalizerFunction> finalizers
) {
    private static final CommandHelper defaultCmdHelper = builder().build();
    public static CommandHelper getDefault() {
        return CommandHelper.defaultCmdHelper;
    }

    private static final CommandHelper ignoreExecutorCmdHelper = builder().ignoreExecutor(true).build();
    public static CommandHelper getDefaultIgnoreExecutor() {
        return CommandHelper.ignoreExecutorCmdHelper;
    }

    public static CommandHelperBuilder builder() {
        return new CommandHelperBuilder();
    }

    public boolean consumeMiddleware(CommandHelperContext ctx) throws CommandSyntaxException {
        for (var middleware : this.middleware) {
            if (!middleware.apply(ctx)) {
                return false;
            }
        }
        return true;
    }

    public void doFinalizers(CommandHelperContext ctx) throws CommandSyntaxException {
        for (var finalizer : this.finalizers) {
            finalizer.finalize(ctx);
        }
    }

    public static interface CtxConsumer {
        void accept(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;
    }

    public static interface CtxConsumerPlayer {
        void accept(CommandContext<CommandSourceStack> ctx, Player player) throws CommandSyntaxException;
    }

    public Command<CommandSourceStack> wrap(final CtxConsumer callback) {
        return ctx -> {
            if (!consumeMiddleware(new CommandHelperContext(ctx, null))) {
                return Command.SINGLE_SUCCESS;
            }
            callback.accept(ctx);
            return Command.SINGLE_SUCCESS;
        };
    }

    public Command<CommandSourceStack> requirePlayer(final CtxConsumerPlayer callback) {
        return ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            Entity executor = ctx.getSource().getExecutor();
            if (ignoreExecutor()) {
                executor = null;
            }

            Player player;
            if (executor != null && executor instanceof Player p) {
                player = p;
            } else if (executor != null) {
                sender.sendRichMessage("<red>Only players may run this command!");
                return Command.SINGLE_SUCCESS;
            } else if (sender instanceof Player p) {
                player = p;
            } else {
                sender.sendRichMessage("<red>Only players may run this command!");
                return Command.SINGLE_SUCCESS;
            }

            if (!consumeMiddleware(new CommandHelperContext(ctx, player))) {
                return Command.SINGLE_SUCCESS;
            }

            callback.accept(ctx, player);
            return Command.SINGLE_SUCCESS;
        };
    }
}