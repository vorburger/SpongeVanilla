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
package org.spongepowered.vanilla.mixin.world;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.configuration.SpongeConfig;
import org.spongepowered.common.interfaces.IMixinWorld;
import org.spongepowered.common.world.border.PlayerBorderListener;
import org.spongepowered.vanilla.block.VanillaBlockSnapshot;
import org.spongepowered.vanilla.interfaces.IBlockSnapshotContainer;

import java.io.File;
import java.util.ArrayList;

@Mixin(World.class)
public abstract class MixinWorld implements org.spongepowered.api.world.World, IMixinWorld, IBlockSnapshotContainer {

    public SpongeConfig<SpongeConfig.WorldConfig> worldConfig;

    @Shadow private net.minecraft.world.border.WorldBorder worldBorder;
    private boolean captureSnapshots;
    private final ArrayList<BlockSnapshot> capturedSnapshots = Lists.newArrayList();
    private BlockSnapshot injectCacheSnapshot;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstructed(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client,
            CallbackInfo ci) {
        String providerName = providerIn.getDimensionName().toLowerCase().replace(" ", "_").replace("[^A-Za-z0-9_]", "");
        this.worldConfig =
                new SpongeConfig<SpongeConfig.WorldConfig>(SpongeConfig.Type.WORLD, new File(Sponge.getConfigDirectory()
                        + File.separator + providerName
                        + File.separator + "dim0", "world.conf"),
                        "sponge");

        this.worldBorder.addListener(new PlayerBorderListener());
    }

    @Inject(method = "spawnEntityInWorld", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunkFromChunkCoords(II)Lnet/minecraft/world/chunk/Chunk;"))
    public void onSpawnEntityInWorld(Entity entity, CallbackInfoReturnable<Boolean> cir, int i, int j, boolean flag) {
        org.spongepowered.api.entity.Entity spongeEntity = (org.spongepowered.api.entity.Entity) entity;
        if (Sponge.getGame().getEventManager().post(SpongeEventFactory.createEntitySpawn(Sponge.getGame(), spongeEntity, spongeEntity.getLocation()))
                && !flag) {
            cir.setReturnValue(false);
        }
    }

    @Surrogate
    public void onSpawnEntityInWorld(Entity entity, CallbackInfoReturnable<Boolean> cir, int i, int j) {
        boolean flag = entity.forceSpawn || entity instanceof EntityPlayer;
        this.onSpawnEntityInWorld(entity, cir, i, j, flag);
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))
    public void createAndStoreBlockSnapshot(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> ci) {
        this.injectCacheSnapshot = null;
        if (this.captureSnapshots) {
            this.injectCacheSnapshot = new VanillaBlockSnapshot((World) (Object) this, pos);
            this.capturedSnapshots.add(this.injectCacheSnapshot);
        }
    }

    @Inject(method = "setBlockState", at = @At(value = "RETURN", ordinal = 3), locals = LocalCapture.PRINT)
    public void purgeSnapshotIfNeeded(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> ci) {
        if (injectCacheSnapshot != null) {
            this.capturedSnapshots.remove(injectCacheSnapshot);
        }
    }

    @Override
    public boolean isCapturingBlockSnapshots() {
        return captureSnapshots;
    }

    @Override
    public void captureBlockSnapshots(boolean captureSnapshots) {
        this.captureSnapshots = captureSnapshots;
    }

    @Override
    public ArrayList<BlockSnapshot> getCapturedSnapshots() {
        return capturedSnapshots;
    }
}
