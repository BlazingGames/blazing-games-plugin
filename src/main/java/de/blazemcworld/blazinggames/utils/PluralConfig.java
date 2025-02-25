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
package de.blazemcworld.blazinggames.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.reflect.TypeToken;

import de.blazemcworld.blazinggames.data.DataStorage;
import de.blazemcworld.blazinggames.data.compression.GZipCompressionProvider;
import de.blazemcworld.blazinggames.data.name.UUIDNameProvider;
import de.blazemcworld.blazinggames.data.storage.GsonStorageProvider;
import net.kyori.adventure.text.format.TextColor;

public class PluralConfig {
    private static final DataStorage<List<MemberData>, UUID> dataStorage = DataStorage.forClass(
        PluralConfig.class, null,
        new GsonStorageProvider<>(new TypeToken<List<MemberData>>() {}.getType()),
        new UUIDNameProvider(), new GZipCompressionProvider()
    );

    public final UUID player;
    public PluralConfig(UUID uuid) {
        this.player = uuid;
    }

    private void requireMeta() {
        if (!dataStorage.hasData(player)) {
            dataStorage.storeData(player, List.of());
        }
    }

    public List<MemberData> getMembers() {
        requireMeta();
        return dataStorage.getData(player);
    }

    public MemberData getMember(String name) {
        requireMeta();
        return getMembers().stream().filter(m -> m.name.equals(name)).findFirst().orElse(null);
    }

    private void addMember(MemberData member) {
        requireMeta();
        List<MemberData> members = new ArrayList<>(dataStorage.getData(player));
        members.add(member);
        dataStorage.storeData(player, members);
    }

    public void addMember(String name) {
        if (getMember(name) != null) return;
        MemberData newMember = new MemberData(name);
        addMember(newMember);
    }

    public void removeMember(String name) {
        requireMeta();
        List<MemberData> members = new ArrayList<>(dataStorage.getData(player));
        members.removeIf(m -> m.name.equals(name));
        dataStorage.storeData(player, members);
    }

    public static Pair<String, String> proxyParse(String text) {
        if (text == null) return null;
        if (!text.contains(proxySplit)) return null;
        if (text.equals(proxySplit)) return null;

        if (text.indexOf(proxySplit) != text.lastIndexOf(proxySplit)) return null; // ensure there is only one occurance
        
        if (text.startsWith(proxySplit)) {
            return new Pair<>("", text.substring(proxySplit.length()).trim());
        } else if (text.endsWith(proxySplit)) {
            return new Pair<>(text.substring(0, text.length() - proxySplit.length()).trim(), "");
        } else {
            String[] parts = text.split(proxySplit);
            return new Pair<>(parts[0], parts[1]);
        }
    }

    public MemberData detectProxiedMember(String text) {
        requireMeta();
        var members = getMembers();
        if (members.isEmpty()) return null;
        return members.stream().filter(m -> {
            if (m.proxyStart == null || m.proxyEnd == null) return false;
            return text.startsWith(m.proxyStart) && text.endsWith(m.proxyEnd);
        }).findFirst().orElse(null);
    }

    public DisplayTag toDisplayTag(String member, PlayerConfig config) {
        requireMeta();
        MemberData data = getMember(member);
        if (data == null) return null;

        return new DisplayTag(
            player,
            config.playerInfo().getUsername(),
            config.playerInfo().isOperator(),
            data.name,
            data.pronouns,
            data.color == null ? config.getNameColor() : TextColor.color(data.color),
            true, config.getSystemName(), config.getSystemTag()
        );
    }





    public static final String proxySplit = "text";
    private void modifyMember(String id, Consumer<MemberData> modifier) {
        MemberData member = getMember(id);
        if (member == null) return;
        modifier.accept(member);
        removeMember(id);
        addMember(member);
    }

    public void rename(String oldName, String newName) {
        modifyMember(oldName, m -> m.name = newName);
    }

    public void setPronouns(String name, String pronouns) {
        modifyMember(name, m -> m.pronouns = pronouns);
    }

    public void setColor(String name, TextColor color) {
        modifyMember(name, m -> m.color = (color == null) ? null : color.value());
    }

    public void setProxy(String name, String proxyStart, String proxyEnd) {
        modifyMember(name, m -> { m.proxyStart = proxyStart; m.proxyEnd = proxyEnd; });
    }
}