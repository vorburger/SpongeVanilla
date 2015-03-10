/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.mixin.block;

import com.google.common.base.Optional;
import mc.Block;
import mc.BlockFalling;
import mc.BlockLiquid;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemBlock;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Block.class, remap = false)
public class MixinBlock implements BlockType {
    @Shadow
    protected mc.IBlockState defaultBlockState;

    @Shadow
    protected boolean needsRandomTick;

    @Shadow
    protected boolean enableStats;

    @Shadow
    protected int lightValue;

    @Shadow
    public boolean isSolidFullCube() {
        return false;
    }

    @Override
    public String getId() {
        return (String) Block.blockRegistry.getNameForObject(this);
    }

    @Override
    public BlockState getDefaultState() {
        return (BlockState) defaultBlockState;
    }

    @Override
    public BlockState getStateFromDataValue(byte b) {
        throw new NotImplementedException("Deprecated");
    }

    @Override
    public boolean getTickRandomly() {
        return needsRandomTick;
    }

    @Override
    public void setTickRandomly(boolean b) {
        needsRandomTick = b;
    }

    @Override
    public boolean isLiquid() {
        return BlockLiquid.class.isAssignableFrom(this.getClass());
    }

    @Override
    public boolean isSolidCube() {
        return isSolidFullCube();
    }

    @Override
    public boolean isAffectedByGravity() {
        return BlockFalling.class.isAssignableFrom(this.getClass());
    }

    @Override
    public boolean areStatisticsEnabled() {
        return enableStats;
    }

    @Override
    public float getEmittedLight() {
        return lightValue / 15f;
    }

    @Override
    public Optional<ItemBlock> getHeldItem() {
        // TODO: Items
        throw new NotImplementedException("");
    }

    @Override
    public Translation getTranslation() {
        // TODO: Translation
        throw new NotImplementedException("");
    }
}
