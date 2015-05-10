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
package org.spongepowered.vanilla;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataManipulator;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.block.BlockBreakEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.player.PlayerBreakBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerPlaceBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.registry.SpongeGameRegistry;
import org.spongepowered.common.util.VecHelper;
import org.spongepowered.vanilla.block.VanillaBlockSnapshot;
import org.spongepowered.vanilla.interfaces.IBlockSnapshotContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class VanillaHooks {

    private VanillaHooks() {
    }

    /**
     * Hook that prepares server logic for the firing of a {@link BlockBreakEvent}.
     *
     * @param world The world
     * @param gameType The gametype
     * @param entityPlayer The player
     * @param pos The position
     * @param blockFacing The face of the block
     * @return The called event
     */
    public static PlayerBreakBlockEvent callPlayerBreakBlockEvent(World world, WorldSettings.GameType gameType, EntityPlayerMP entityPlayer,
                                                                  BlockPos pos, EnumFacing blockFacing) {
        boolean preCancelEvent = false;
        if (gameType.isCreative() && entityPlayer.getHeldItem() != null && entityPlayer.getHeldItem().getItem() instanceof ItemSword) {
            preCancelEvent = true;
        }
        if (gameType.isAdventure()) {
            if (gameType == WorldSettings.GameType.SPECTATOR) {
                preCancelEvent = true;
            }

            if (!entityPlayer.isAllowEdit()) {
                ItemStack itemstack = entityPlayer.getCurrentEquippedItem();
                if (itemstack == null || !itemstack.canDestroy(world.getBlockState(pos).getBlock())) {
                    preCancelEvent = true;
                }
            }
        }

        // Tell client the block is gone immediately then process events
        if (world.getTileEntity(pos) == null) {
            S23PacketBlockChange packet = new S23PacketBlockChange(world, pos);
            packet.blockState = Blocks.air.getDefaultState();
            entityPlayer.playerNetServerHandler.sendPacket(packet);
        }

        // TODO Support replacement block to place when break succeeds
        // Post the block break event
        PlayerBreakBlockEvent event = SpongeEventFactory.createPlayerBreakBlock(Sponge.getGame(), new Cause(null, entityPlayer, null),
                (Player) entityPlayer, SpongeGameRegistry.directionMap.inverse().get(blockFacing), new Location((Extent) world,
                        VecHelper.toVector(pos)), new VanillaBlockSnapshot(world, pos, world.getBlockState(pos)), 0);
        event.setCancelled(preCancelEvent);
        Sponge.getGame().getEventManager().post(event);
        if (event.isCancelled()) {
            // Let the client know the block still exists
            entityPlayer.playerNetServerHandler.sendPacket(new S23PacketBlockChange(world, pos));

            // Update any tile entity data for this block
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity != null) {
                Packet packet = tileentity.getDescriptionPacket();
                if (packet != null) {
                    entityPlayer.playerNetServerHandler.sendPacket(packet);
                }
            }
        }
        return event;
    }

    public static boolean callPlayerPlaceBlockEvent(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        final int prevStackSize = stack.stackSize;
        // TODO Copy ItemStack data into container
        final Collection<DataManipulator<?>> prevManipulators = ((org.spongepowered.api.item.inventory.ItemStack)stack).getManipulators();
        ((IBlockSnapshotContainer) world).captureBlockSnapshots(true);
        boolean success = stack.getItem().onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
        ((IBlockSnapshotContainer) world).captureBlockSnapshots(false);
        final List<BlockSnapshot> copiedSnapshots = (ArrayList<BlockSnapshot>) ((IBlockSnapshotContainer) world).getCapturedSnapshots().clone();
        ((IBlockSnapshotContainer) world).getCapturedSnapshots().clear();
        // If item use is successful, process player block placement
        if (success) {
            //TODO Copy over old data for itemstack for event
            final int newStackSize = stack.stackSize;
            final Collection<DataManipulator<?>> newManipulators = ((org.spongepowered.api.item.inventory.ItemStack) stack).getManipulators();
            final PlayerPlaceBlockEvent event = SpongeEventFactory.createPlayerPlaceBlock(Sponge.getGame(), new Cause(null, player, null), (Player) player, new Location((Extent) world, VecHelper.toVector(pos)), copiedSnapshots.get(0), SpongeGameRegistry.directionMap.inverse().get(side));
            success = !Sponge.getGame().getEventManager().post(event);
            if (!success) {
                for (BlockSnapshot snapshot : copiedSnapshots) {
                    // TODO Restore the snapshot
                }
            } else {
                //TODO Apply new data to itemstack
                for (BlockSnapshot snapshot : copiedSnapshots) {
                    //TODO Apply snapshot data
                }
                player.addStat(StatList.objectUseStats[Item.getIdFromItem(stack.getItem())], 1);
            }
        }
        return success;
    }
}
