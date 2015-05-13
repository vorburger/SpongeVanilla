package org.spongepowered.vanilla.mixin.block;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(BlockDaylightDetector.class)
public class MixinBlockDaylightDetector {

    @Inject(method = "onBlockActivated", at = @At(value = "FIELD"), cancellable = true)
    public void callPlayerPlaceBlockEvent(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float
            hitY, float hitZ) {

    }
}
