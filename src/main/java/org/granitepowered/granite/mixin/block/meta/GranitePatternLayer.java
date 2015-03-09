package org.granitepowered.granite.mixin.block.meta;

import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.block.data.Banner;
import org.spongepowered.api.block.meta.BannerPatternShape;
import org.spongepowered.api.entity.living.animal.DyeColor;
import org.spongepowered.api.service.persistence.DataSource;
import org.spongepowered.api.service.persistence.data.DataContainer;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class GranitePatternLayer implements Banner.PatternLayer {

    private final BannerPatternShape bannerPatternShape;
    private final DyeColor dyeColor;

    private GranitePatternLayer(BannerPatternShape bannerPatternShape, DyeColor dyeColor) {
        this.bannerPatternShape = bannerPatternShape;
        this.dyeColor = dyeColor;
    }

    @Override
    public BannerPatternShape getId() {
        return this.bannerPatternShape;
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }

    @Override
    public DataContainer toContainer() {
        throw new NotImplementedException("");
    }

    @Override
    public void serialize(DataSource dataSource) {
        throw new NotImplementedException("");
    }
}
