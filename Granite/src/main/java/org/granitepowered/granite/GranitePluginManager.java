package org.granitepowered.granite;

import com.google.common.base.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Collection;

@NonnullByDefault
public class GranitePluginManager implements PluginManager {

    @Override
    public Optional<PluginContainer> fromInstance(Object o) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<PluginContainer> getPlugin(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Logger getLogger(PluginContainer pluginContainer) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        throw new NotImplementedException("");
    }

    @Override
    public boolean isLoaded(String s) {
        throw new NotImplementedException("");
    }
}
