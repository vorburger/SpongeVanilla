package org.granitepowered.granite.particle;

import com.flowpowered.math.vector.Vector3f;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.awt.Color;

@NonnullByDefault
public class GraniteParticleEffect implements ParticleEffect {

    private final ParticleType particleType;
    private final Vector3f motion;
    private final Vector3f offset;
    private final int count;

    public GraniteParticleEffect(ParticleType particleType, Vector3f motion, Vector3f offset, int count) {
        this.particleType = particleType;
        this.motion = motion;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public ParticleType getType() {
        return this.particleType;
    }

    @Override
    public Vector3f getMotion() {
        return this.motion;
    }

    @Override
    public Vector3f getOffset() {
        return this.offset;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    public static class Colorable extends GraniteParticleEffect implements ParticleEffect.Colorable {

        private final Color color;

        public Colorable(ParticleType particleType, Vector3f motion, Vector3f offset, int count, Color color) {
            super(particleType, motion, offset, count);
            this.color = color;
        }

        @Override
        public Color getColor() {
            return this.color;
        }
    }

    public static class Resizeable extends GraniteParticleEffect implements ParticleEffect.Resizable {

        public final float size;

        public Resizeable(ParticleType particleType, Vector3f motion, Vector3f offset, int count, float size) {
            super(particleType, motion, offset, count);
            this.size = size;
        }

        @Override
        public float getSize() {
            return this.size;
        }
    }

    public static class Material extends GraniteParticleEffect implements ParticleEffect.Material {

        public final ItemStack itemStack;

        public Material(ParticleType particleType, Vector3f motion, Vector3f offset, int count, ItemStack itemStack) {
            super(particleType, motion, offset, count);
            this.itemStack = itemStack;
        }

        @Override
        public ItemStack getItem() {
            return this.itemStack;
        }
    }

    public static class Note extends GraniteParticleEffect implements ParticleEffect.Note {

        public final float note;

        public Note(ParticleType particleType, Vector3f motion, Vector3f offset, int count, float note) {
            super(particleType, motion, offset, count);
            this.note = note;
        }

        @Override
        public float getNote() {
            return this.note;
        }
    }
}
