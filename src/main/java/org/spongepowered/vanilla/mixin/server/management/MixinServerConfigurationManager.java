/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.vanilla.mixin.server.management;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerRespawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.text.SpongeTexts;

import javax.annotation.Nullable;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager {

    @Shadow private MinecraftServer mcServer;
    @Nullable private IChatComponent joinMessage;

    private EntityPlayerMP newPlayer;
    private boolean isBedSpawn;

    @Redirect(method = "initializeConnectionToPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/ServerConfigurationManager;sendChatMsg(Lnet/minecraft/util/IChatComponent;)V"))
    public void initializeConnectionToPlayerRedirectSendChatMsg(ServerConfigurationManager this$0, IChatComponent component) {
        this.joinMessage = component;
    }

    @Inject(method = "initializeConnectionToPlayer", at = @At("RETURN"))
    public void initializeConnectionToPlayerInject(NetworkManager netManager, EntityPlayerMP player, CallbackInfo ci) {
        PlayerJoinEvent event = SpongeEventFactory.createPlayerJoin(Sponge.getGame(), (Player) player, SpongeTexts.toText(this.joinMessage));
        this.joinMessage = null;
        Sponge.getGame().getEventManager().post(event);

        // Send (possibly changed) join message
        ((Server) this.mcServer).broadcastMessage(event.getJoinMessage());
    }

    // TODO: This player entity recreation will likely be removed at some point
    // TODO: Make sure this event is accounted for when that happens
    // TODO: See the clonePlayer() mixin in MixinEntityPlayer, this will need to be reworked

    // TODO: We also need to take care of cross-world teleportation, saving that for when world changes are in

    @Redirect(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;clonePlayer(Lnet/minecraft/entity/player/EntityPlayer;Z)V"))
    public void recreatePlayerEntityPlayerGrab(EntityPlayerMP this$0, EntityPlayer oldPlayer, boolean respawnFromEnd) {
        // This redirect is purely to grab hold of the new player (this$0) for use below
        newPlayer = this$0;
        isBedSpawn = false;
    }

    @Inject(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;setSpawnPoint(Lnet/minecraft/util/BlockPos;Z)V"))
    public void recreatePlayerEntityBedCheck(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd, CallbackInfo ci) {
        isBedSpawn = true;
    }

    @Inject(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/ChunkProviderServer;loadChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    public void recreatePlayerEntity(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd, CallbackInfoReturnable<Chunk> ci) {
        PlayerRespawnEvent event = SpongeEventFactory.createPlayerRespawn(Sponge.getGame(), (Player) newPlayer, isBedSpawn, ((Player) newPlayer).getLocation());
        Sponge.getGame().getEventManager().post(event);
        ((Player) newPlayer).setLocation(event.getRespawnLocation());
    }
}
