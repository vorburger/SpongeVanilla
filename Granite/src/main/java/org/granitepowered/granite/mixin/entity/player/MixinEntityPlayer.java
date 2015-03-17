package org.granitepowered.granite.mixin.entity.player;

import mc.*;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityPlayer.class, remap = false)
@Implements(@Interface(iface = Human.class, prefix = "human$"))
public class MixinEntityPlayer extends EntityLivingBase {

    @Shadow
    public Container inventoryContainer;

    @Shadow
    public Container openContainer;

    @Shadow
    protected FoodStats foodStats;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    public double human$getFoodLevel() {
        return this.foodStats.foodLevel;
    }

    public void human$setFoodLevel(double foodLevel) {
        this.foodStats.foodLevel = (int) foodLevel;
    }

    public double human$getSaturation() {
        return this.foodStats.foodSaturationLevel;
    }

    public void human$setSaturation(double saturation) {
        this.foodStats.foodSaturationLevel = (float) saturation;
    }

    public boolean human$isViewingInventory() {
        return this.openContainer == this.inventoryContainer;
    }

}
