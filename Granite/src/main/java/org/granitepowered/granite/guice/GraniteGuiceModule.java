package org.granitepowered.granite.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.granitepowered.granite.Granite;
import org.granitepowered.granite.GraniteEventManager;
import org.granitepowered.granite.registry.GraniteGameRegistry;
import org.granitepowered.granite.GranitePluginManager;
import org.granitepowered.granite.GraniteScheduler;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.service.scheduler.Scheduler;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.io.File;

import javax.inject.Provider;

@NonnullByDefault
public class GraniteGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        ConfigDir sharedConfigDir = new ConfigDirAnnotation(true);

        bind(Granite.class).in(Scopes.SINGLETON);

        bind(Game.class).toProvider(GraniteProvider.class).in(Scopes.SINGLETON);
        bind(PluginManager.class).to(GranitePluginManager.class).in(Scopes.SINGLETON);
        bind(GameRegistry.class).to(GraniteGameRegistry.class).in(Scopes.SINGLETON);
        bind(EventManager.class).to(GraniteEventManager.class).in(Scopes.SINGLETON);
        bind(Scheduler.class).to(GraniteScheduler.class).in(Scopes.SINGLETON);
        bind(File.class).annotatedWith(sharedConfigDir).toProvider(GlobalPluginDataDirProvider.class).in(Scopes.SINGLETON);
    }

    /**
     * Provides GraniteServer. This is used instead of <code>to(GraniteServer.class)</code>
     * because otherwise it would be immediately loaded by Guice and then class rewriting
     * would fail.
     */
    private static class GraniteProvider implements Provider<Game> {

        @Override
        public Game get() {
            return new Granite();
        }

    }

    private static class GlobalPluginDataDirProvider implements Provider<File> {

        @Override
        public File get() {
            return Granite.getInstance().getServerConfig().getPluginDataDirectory();
        }

    }

}

