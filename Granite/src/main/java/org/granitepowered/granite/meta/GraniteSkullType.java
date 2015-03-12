package org.granitepowered.granite.meta;

import org.spongepowered.api.block.meta.SkullType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class GraniteSkullType implements SkullType {

    private final byte id;
    private final String name;

    private GraniteSkullType(byte id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public byte getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
