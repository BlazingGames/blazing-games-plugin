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

import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import de.blazemcworld.blazinggames.utils.EmojiRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class EmojiListCommand {
    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("emojilist")
            .executes(ctx -> {
                CommandSender sender = ctx.getSource().getSender();
                sender.sendMessage(Component.text("Emoji:", TextColor.color(0xb7e8e4)));
                EmojiRowIterator iterator = new EmojiRowIterator(15);
                while (iterator.hasNext()) {
                    sender.sendMessage(iterator.next());
                }
                sender.sendMessage(Component.empty());
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }

    public static class EmojiRowIterator implements Iterator<Component> {
        private final int len;
        private final List<String> emoji;
        private final int totalEmojis;
        private final int totalRows;
        public EmojiRowIterator(int len) {
            this.len = len;
            this.emoji = EmojiRegistry.emojiMap.keySet().stream().toList();
            this.totalEmojis = emoji.size();
            this.totalRows = (totalEmojis - (totalEmojis % len)) / len + 1;
        }

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < totalRows;
        }

        @Override
        public Component next() {
            if (!hasNext()) return null;

            Component out = Component.empty();
            for (int i = 0; i < len; i++) {
                int emojiIndex = i + (index * len);
                if (emojiIndex >= totalEmojis) break;
                String e = emoji.get(emojiIndex);
                out = out.append(EmojiRegistry.render(e, EmojiRegistry.emojiMap.get(e)));
                if (i < len - 1) {
                    out = out.appendSpace();
                }
            }
            index++;
            return out;
        }
    }
}