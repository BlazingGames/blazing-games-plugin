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
package de.blazemcworld.blazinggames.computing.motor;

import com.destroystokyo.paper.profile.PlayerProfile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class HeadComputerMotor implements IComputerMotor {
    public final PlayerProfile profile;

    public HeadComputerMotor(PlayerProfile playerProfile) {
        this.profile = playerProfile;
    }

    @Override
    public boolean usesActor() {
        return false;
    }

    @Override
    public EntityType actorEntityType() {
        return null;
    }

    @Override
    public void applyActorProperties(Entity actor) {
    }

    @Override
    public void moveActor(Entity actor, Location newLocation) {
    }

    @Override
    public boolean usesBlock() {
        return true;
    }

    @Override
    public Material blockMaterial() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public void applyPropsToBlock(Block block) {
        Skull state = (Skull)block.getState();
        state.setPlayerProfile(this.profile);
        state.update();
    }
}
