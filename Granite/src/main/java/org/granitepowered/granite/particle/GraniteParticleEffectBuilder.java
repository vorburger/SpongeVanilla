package org.granitepowered.granite.particle;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3f;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.awt.Color;

@NonnullByDefault
public class GraniteParticleEffectBuilder implements ParticleEffectBuilder {

    protected final ParticleType particleType;
    protected Vector3f motion = Vector3f.ZERO;
    protected Vector3f offset = Vector3f.ZERO;
    protected int count = 1;

    public GraniteParticleEffectBuilder(ParticleType particleType) {
        this.particleType = particleType;
    }

    @Override
    public GraniteParticleEffectBuilder motion(Vector3f motion) {
        checkNotNull(motion, "The motion vector cannot be null! Use Vector3f.ZERO instead!");
        this.motion = motion;
        return this;
    }

    @Override
    public GraniteParticleEffectBuilder offset(Vector3f offset) {
        checkNotNull(offset, "The offset vector cannot be null! Use Vector3f.ZERO instead!");
        this.offset = offset;
        return this;
    }

    @Override
    public GraniteParticleEffectBuilder count(int count) throws IllegalArgumentException {
        checkArgument(count > 0, "The count has to be greater then zero!");
        this.count = count;
        return this;
    }

    @Override
    public ParticleEffect build() {
        return new GraniteParticleEffect(this.particleType, this.motion, this.offset, this.count);
    }

    public static class BuilderColorable extends GraniteParticleEffectBuilder implements ParticleEffectBuilder.Colorable {

        private Color color;

        public BuilderColorable(ParticleType.Colorable type) {
            super(type);
            this.color = type.getDefaultColor();
        }

        @Override
        public Colorable color(Color color) {
            checkNotNull(color, "The color cannot be null!");
            this.color = color;
            return this;
        }

        @Override
        public BuilderColorable motion(Vector3f motion) {
            return (BuilderColorable) super.motion(motion);
        }

        @Override
        public BuilderColorable offset(Vector3f motion) {
            return (BuilderColorable) super.offset(motion);
        }

        @Override
        public BuilderColorable count(int count) {
            return (BuilderColorable) super.count(count);
        }

        @Override
        public ParticleEffect.Colorable build() {
            return new GraniteParticleEffect.Colorable(this.particleType, this.motion, this.offset, this.count, this.color);
        }

    }

    public static class BuilderResizable extends GraniteParticleEffectBuilder implements ParticleEffectBuilder.Resizable {

        private float size;

        public BuilderResizable(ParticleType.Resizable type) {
            super(type);
            this.size = type.getDefaultSize();
        }

        @Override
        public BuilderResizable size(float size) {
            checkArgument(size >= 0f, "The size has to be greater or equal to zero!");
            this.size = size;
            return this;
        }

        @Override
        public BuilderResizable motion(Vector3f motion) {
            return (BuilderResizable) super.motion(motion);
        }

        @Override
        public BuilderResizable offset(Vector3f offset) {
            return (BuilderResizable) super.offset(offset);
        }

        @Override
        public BuilderResizable count(int count) {
            return (BuilderResizable) super.count(count);
        }

        @Override
        public ParticleEffect.Resizable build() {
            return new GraniteParticleEffect.Resizeable(this.particleType, this.motion, this.offset, this.count, this.size);
        }

    }

    public static class BuilderNote extends GraniteParticleEffectBuilder implements ParticleEffectBuilder.Note {

        private float note;

        public BuilderNote(ParticleType.Note type) {
            super(type);
            this.note = type.getDefaultNote();
        }

        @Override
        public BuilderNote note(float note) {
            checkArgument(note >= 0f && note <= 24f, "The note has to scale between 0 and 24!");
            this.note = note;
            return this;
        }

        @Override
        public BuilderNote motion(Vector3f motion) {
            return (BuilderNote) super.motion(motion);
        }

        @Override
        public BuilderNote offset(Vector3f offset) {
            return (BuilderNote) super.offset(offset);
        }

        @Override
        public BuilderNote count(int count) {
            return (BuilderNote) super.count(count);
        }

        @Override
        public ParticleEffect.Note build() {
            return new GraniteParticleEffect.Note(this.particleType, this.motion, this.offset, this.count, this.note);
        }

    }

    public static class BuilderMaterial extends GraniteParticleEffectBuilder implements ParticleEffectBuilder.Material {

        private ItemStack item;

        public BuilderMaterial(ParticleType.Material type) {
            super(type);
            this.item = type.getDefaultItem();
        }

        @Override
        public BuilderMaterial item(ItemStack item) {
            checkNotNull(item, "The item stack cannot be null!");
            this.item = item;
            return this;
        }

        @Override
        public Material itemType(ItemType item) {
            checkNotNull(item, "The item type cannot be null!");
            this.item = ItemStack.class.cast(new mc.ItemStack((mc.Item) item));
            return null;
        }

        @Override
        public BuilderMaterial motion(Vector3f motion) {
            return (BuilderMaterial) super.motion(motion);
        }

        @Override
        public BuilderMaterial offset(Vector3f offset) {
            return (BuilderMaterial) super.offset(offset);
        }

        @Override
        public BuilderMaterial count(int count) {
            return (BuilderMaterial) super.count(count);
        }

        @Override
        public ParticleEffect.Material build() {
            return new GraniteParticleEffect.Material(this.particleType, this.motion, this.offset, this.count, this.item);
        }

    }

}
