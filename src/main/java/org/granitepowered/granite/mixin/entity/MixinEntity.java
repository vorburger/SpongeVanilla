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

package org.granitepowered.granite.mixin.entity;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumSet;
import java.util.UUID;

import javax.annotation.Nullable;

@NonnullByDefault
@Mixin(value = mc.Entity.class, remap = false)
public abstract class MixinEntity implements Entity {
    @Shadow
    public mc.World worldObj;
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public float width;
    @Shadow
    public float height;
    @Shadow
    public boolean isDead;
    @Shadow
    public boolean onGround;
    @Shadow
    public int fireResistance;
    @Shadow
    public mc.Entity riddenByEntity;
    @Shadow
    public mc.Entity ridingEntity;
    @Shadow
    protected UUID entityUniqueID;
    private EntityType entityType;
    private boolean teleporting;
    private Entity teleportVehicle;
    private float originalWidth;
    private float originalHeight;
    @Shadow
    private int fire;

    @Shadow
    public abstract void mountEntity(mc.Entity entity);

    @Shadow
    public abstract void setLocationAndAngles(double x, double y, double z, float yaw, float pitch);

    @Override
    public World getWorld() {
        return (World) this.worldObj;
    }

    @Override
    public Location getLocation() {
        return new Location(getWorld(), new Vector3d(this.posX, this.posY, this.posZ));
    }

    @Override
    public boolean setLocation(Location location) {
        // TODO: move between worlds
        setLocationAndAngles(location.getPosition().getX(), location.getPosition().getY(), location.getPosition().getZ(), rotationYaw, rotationPitch);
        return true;
    }

    @Override
    public boolean setLocationAndRotation(Location location, Vector3f vector3f, EnumSet<RelativePositions> enumSet) {
        // TODO: move between worlds
        // The vector3f passed is {yaw, pitch, roll}
        // Entities don't have roll, so that's ignored
        setLocationAndAngles(location.getPosition().getX(), location.getPosition().getY(), location.getPosition().getZ(), vector3f.getX(), vector3f.getY());
        return true;
    }

    @Override
    public Vector3f getRotation() {
        return new Vector3f(this.rotationYaw, this.rotationPitch, 0);
    }

    @Override
    public void setRotation(Vector3f vector3f) {
        rotationPitch = vector3f.getX() % 360;
        rotationYaw = vector3f.getY() % 360;
    }

    @Override
    public Vector3d getVelocity() {
        return new Vector3d(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public void setVelocity(Vector3d vector3d) {
        this.motionX = vector3d.getX();
        this.motionY = vector3d.getY();
        this.motionZ = vector3d.getZ();
    }

    @Override
    public Optional<Entity> getPassenger() {
        return Optional.fromNullable((Entity) this.riddenByEntity);
    }

    @Override
    public Optional<Entity> getVehicle() {
        return Optional.fromNullable((Entity) this.ridingEntity);
    }

    @Override
    public Entity getBaseVehicle() {
        if (this.ridingEntity == null) {
            return this;
        }

        Entity baseVehicle = this.getVehicle().get();
        while (baseVehicle != null) {
            baseVehicle = baseVehicle.getVehicle().get();
        }
        return baseVehicle;
    }

    @Override
    public boolean setPassenger(@Nullable Entity entity) {
        if (this.ridingEntity == null) {
            if (entity == null) {
                return true;
            }

            entity.setVehicle(this);
        } else {
            this.getVehicle().get().setVehicle(null);

            if (entity != null) {
                entity.setVehicle(this);
            }
        }
        return true;
    }

    @Override
    public boolean setVehicle(@Nullable Entity entity) {
        mountEntity((mc.Entity) entity);
        return true;
    }

    @Override
    public float getBase() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public float getScale() {
        if (this.originalWidth == 0 || this.originalHeight == 0) {
             this.originalWidth = this.width;
             this.originalHeight = this.height;
         }
         double scaleWidth = this.width / this.originalWidth;
         double scaleHeight = this.height / this.originalHeight;
         return (float) (scaleHeight + scaleWidth) / 2;
    }

    @Override
    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public boolean isRemoved() {
        return this.isDead;
    }

    @Override
    public boolean isLoaded() {
        // TODO: isLoaded
        throw new NotImplementedException("");
    }

    @Override
    public void remove() {
        this.isDead = true;
    }

    @Override
    public int getFireTicks() {
        return this.fire;
    }

    @Override
    public void setFireTicks(int fireTicks) {
        this.fire = fireTicks;
    }

    @Override
    public int getFireDelay() {
        return this.fireResistance;
    }

    @Override
    public boolean isPersistent() {
        // TODO: isPersistent
        throw new NotImplementedException("");
    }

    @Override
    public void setPersistent(boolean b) {
        // TODO: setPersistent
        throw new NotImplementedException("");
    }

    @Override
    public <T> Optional<T> getData(Class<T> aClass) {
        // TODO: getData
        throw new NotImplementedException("");
    }

    @Override
    public EntityType getType() {
        // TODO: getType
        throw new NotImplementedException("");
    }

    @Override
    public EntitySnapshot getSnapshot() {
        // TODO: getSnapshot
        throw new NotImplementedException("");
    }

    @Override
    public UUID getUniqueId() {
        return this.entityUniqueID;
    }
}
