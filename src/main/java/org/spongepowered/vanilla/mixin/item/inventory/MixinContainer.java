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
package org.spongepowered.vanilla.mixin.item.inventory;

import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.entity.player.PlayerDropItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.Sponge;

@Mixin(Container.class)
public class MixinContainer {
    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    public void injectDropItemSurvivalOutsideInventory(int slotId, int clickedButton, int mode, EntityPlayer playerIn, CallbackInfoReturnable<ItemStack> ci) {
        InventoryPlayer inventoryplayer = playerIn.inventory;

        if ((mode == 0 || mode == 1) && (clickedButton == 0 || clickedButton == 1) && slotId == -999) {
            if (inventoryplayer.getItemStack() != null) {
                net.minecraft.item.ItemStack stack = inventoryplayer.getItemStack();
                if (clickedButton == 1) {
                    stack = inventoryplayer.getItemStack().copy();
                    stack.stackSize = 1;
                }
                PlayerDropItemEvent event = SpongeEventFactory.createPlayerDropItem(Sponge.getGame(), (Player) playerIn, null,
                                                                                    Sets.newHashSet((ItemStack) stack));

                if (Sponge.getGame().getEventManager().post(event)) {
                    // Forge will (for some reason) consume the item even if it was cancelled, so we will consume it explicitly here as well (see comments in MixinNetHandlerPlayServer)
                    // TODO: this is stupid, change?

                    if (clickedButton == 0) {
                        inventoryplayer.setItemStack(null);
                    }

                    if (clickedButton == 1) {
                        inventoryplayer.getItemStack().splitStack(1);

                        if (inventoryplayer.getItemStack().stackSize == 0) {
                            inventoryplayer.setItemStack(null);
                        }
                    }

                    ci.cancel();
                }
            }
        }
    }
}
