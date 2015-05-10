package org.spongepowered.vanilla.mixin.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.vanilla.VanillaHooks;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    public void onOnItemUseHead(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(VanillaHooks.callPlayerPlaceBlockEvent((ItemStack) (Object) this, playerIn,  worldIn, pos, side, hitX, hitY, hitZ));
    }
}
