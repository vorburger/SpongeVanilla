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

package org.granitepowered.granite.mixin.entity.living;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Optional;
import mc.*;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.potion.*;
import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(value = EntityLivingBase.class, remap = false)
@Implements(@Interface(iface = Living.class, prefix = "living$"))
public class MixinEntityLivingBase extends Entity {
    private int maxAir = 300;
    @Shadow
    protected float lastDamage;

    @Shadow
    public int maxHurtResistantTime;

    @Shadow
    public EntityLivingBase lastAttacker;

    @Shadow
    public float getMaxHealth() {
        return 0;
    }

    @Shadow
    public IAttributeInstance getEntityAttribute(IAttribute attribute) {
        return null;
    }

    @Shadow
    public void addPotionEffect(mc.PotionEffect potionEffect) {

    }

    @Shadow
    public void removePotionEffect(int id) {

    }

    @Shadow
    public boolean isPotionActive(Potion potion) {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Shadow
    public Collection getActivePotionEffects() {
        return null;
    }

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }


    public float getHealth() {
        return (float) dataWatcher.getWatchedObject(6).watchedObject;
    }

    public void setHealth(float health) {
        dataWatcher.updateObject(6, health);
    }

    public void living$damage(double amount) {
        Living thisEntity = (Living) this;
        DamageSource source;
        if (thisEntity instanceof Human) {
            source = DamageSource.causePlayerDamage((EntityPlayerMP) thisEntity);
        } else {
            source = DamageSource.causeMobDamage((EntityLivingBase) thisEntity);
        }

        if (thisEntity instanceof EntityDragon) {
            ((EntityDragon) thisEntity).attackEntityFrom(source, (float) amount);
        } else {
            attackEntityFrom(source, (float) amount);
        }
    }

    public double living$getHealth() {
        return getHealth();
    }

    public void living$setHealth(double health) {
        Living thisEntity = (Living) this;
        setHealth((float) health);

        if (thisEntity instanceof EntityPlayerMP && health == 0) {
            ((EntityPlayerMP) thisEntity).onDeath(DamageSource.generic);
        }
    }

    public double living$getMaxHealth() {
        return getMaxHealth();
    }

    public void living$setMaxHealth(double maxHealth) {
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);

        if (getHealth() > maxHealth) {
            setHealth((float) maxHealth);
        }
    }

    public void living$addPotionEffect(org.spongepowered.api.potion.PotionEffect potionEffect, boolean force) {
        if (living$hasPotionEffect(potionEffect.getType())) {
            if (!force) {
                return;
            }
            living$removePotionEffect(potionEffect.getType());
        }

        addPotionEffect(new mc.PotionEffect((mc.PotionEffect) potionEffect));
    }

    public void living$addPotionEffects(Collection<PotionEffect> potionEffects, boolean force) {
        for (PotionEffect effect : potionEffects) {
            living$addPotionEffect(effect, force);
        }
    }

    public void living$removePotionEffect(PotionEffectType potionEffectType) {
        removePotionEffect(((Potion) potionEffectType).id);
    }

    public boolean living$hasPotionEffect(PotionEffectType potionEffectType) {
        return isPotionActive((Potion) potionEffectType);
    }

    public List<org.spongepowered.api.potion.PotionEffect> living$getPotionEffects() {
        List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
        for (Object obj : getActivePotionEffects()) {
            potionEffects.add((PotionEffect) obj);
        }
        return potionEffects;
    }

    public Optional<Living> living$getLastAttacker() {
        return Optional.fromNullable((Living) lastAttacker);
    }

    public void living$setLastAttacker(@Nullable Living lastAttacker) {
        this.lastAttacker = (EntityLivingBase) lastAttacker;
    }

    public double living$getEyeHeight() {
        return getEyeHeight();
    }

    public Vector3f living$getEyeLocation() {
        return ((Living) this).getLocation().getPosition().add(0, getEyeHeight(), 0).toFloat();
    }

    public int living$getRemainingAir() {
        return (int) dataWatcher.getWatchedObject(1).watchedObject;
    }

    public void living$setRemainingAir(int air) {
        dataWatcher.updateObject(1, air);
    }

    public int living$getMaxAir() {
        return this.maxAir;
    }

    public void living$setMaxAir(int air) {
        this.maxAir = air;
    }

    public double getLastDamage() {
        return this.lastDamage;
    }

    public void living$setLastDamage(double damage) {
        this.lastDamage = (float) damage;
    }

    public int living$getInvulnerabilityTicks() {
        return this.hurtResistantTime;
    }

    public void living$setInvulnerabilityTicks(int ticks) {
        this.hurtResistantTime = ticks;
    }

    public int living$getMaxInvulnerabilityTicks() {
        return this.maxHurtResistantTime;
    }

    public void living$setMaxInvulnerabilityTicks(int ticks) {
        this.maxHurtResistantTime = ticks;
    }

    public String living$getCustomName() {
        return (String) dataWatcher.getWatchedObject(2).watchedObject;
    }

    public void living$setCustomName(String name) {
        if (name == null) {
            name = "";
        }

        if (name.length() > 64) {
            name = name.substring(0, 64);
        }

        dataWatcher.updateObject(2, name);
    }

    public boolean living$isCustomNameVisible() {
        return ((byte) dataWatcher.getWatchedObject(3).watchedObject) == 1;
    }

    public void living$setCustomNameVisible(boolean visible) {
        dataWatcher.updateObject(3, visible ? 1 : 0);
    }

    public boolean living$isInvisible() {
        return this.getFlag(5);
    }

    public void living$setInvisible(boolean invisible) {
        this.setFlag(5, invisible);
    }
}
