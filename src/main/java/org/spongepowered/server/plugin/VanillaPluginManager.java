/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.server.plugin;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Singleton;

import ch.vorburger.hotea.minecraft.api.HotPluginManager;
import net.minecraft.launchwrapper.Launch;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactoryUtils;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.event.SpongeEventManager;
import org.spongepowered.plugin.meta.PluginMetadata;
import org.spongepowered.plugin.meta.SpongeExtension;
import org.spongepowered.server.launch.plugin.PluginCandidate;
import org.spongepowered.server.launch.plugin.PluginScanner;
import org.spongepowered.server.launch.plugin.VanillaLaunchPluginManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
public class VanillaPluginManager implements PluginManager, HotPluginManager {

    private final Map<String, PluginContainer> plugins = new HashMap<>();
    private final Map<Object, PluginContainer> pluginInstances = new IdentityHashMap<>();

    private void registerPlugin(PluginContainer plugin) {
        this.plugins.put(plugin.getId(), plugin);
        plugin.getInstance().ifPresent(instance -> this.pluginInstances.put(instance, plugin));
    }

    private void unregisterPlugin(PluginContainer plugin) { // HotPluginManager
        this.plugins.remove(plugin.getId());
        plugin.getInstance().ifPresent(instance -> this.pluginInstances.remove(instance));
    }

    public void loadPlugins() throws IOException {
        for (PluginContainer container : SpongeImpl.getInternalPlugins()) {
            registerPlugin(container);
        }

        Map<String, PluginCandidate> candidateMap = VanillaLaunchPluginManager.getPlugins();
        ClassLoader classLoader = this.getClass().getClassLoader();
        loadPlugins(candidateMap, classLoader, true);
    }

    private Iterable<PluginContainer> loadPlugins(Map<String, PluginCandidate> candidateMap, ClassLoader classLoader, boolean addSourceToLaunchClassLoader) {
    	Collection<PluginContainer> loadedPluginContainers = new ArrayList<PluginContainer>(candidateMap.size());
        try {
            for (PluginCandidate candidate : PluginSorter.sort(checkRequirements(candidateMap))) {
                loadPlugin(candidate, classLoader, addSourceToLaunchClassLoader).ifPresent(p -> loadedPluginContainers.add(p));
            }
        } catch (Throwable e) {
            throw PluginReporter.crash(e, candidateMap.values());
        }
        return loadedPluginContainers; 
    }
  
    private Set<PluginCandidate> checkRequirements(Map<String, PluginCandidate> candidates) {
        Set<PluginCandidate> successfulCandidates = new HashSet<>(candidates.size());
        List<PluginCandidate> failedCandidates = new ArrayList<>();

        for (PluginCandidate candidate : candidates.values()) {
            if (candidate.collectDependencies(this.plugins, candidates)) {
                successfulCandidates.add(candidate);
            } else {
                failedCandidates.add(candidate);
            }
        }

        if (failedCandidates.isEmpty()) {
            return successfulCandidates; // Nothing to do, all requirements satisfied
        }

        PluginCandidate candidate;
        boolean updated;
        while (true) {
            updated = false;
            Iterator<PluginCandidate> itr = successfulCandidates.iterator();
            while (itr.hasNext()) {
                candidate = itr.next();
                if (candidate.updateRequirements()) {
                    updated = true;
                    itr.remove();
                    failedCandidates.add(candidate);
                }
            }

            if (updated) {
                // Update failed candidates as well
                failedCandidates.forEach(PluginCandidate::updateRequirements);
            } else {
                break;
            }
        }

        for (PluginCandidate failed : failedCandidates) {
            if (failed.isInvalid()) {
                SpongeImpl.getLogger().error("Plugin '{}' from {} cannot be loaded because it is invalid", failed.getId(), failed.getDisplaySource());
            } else {
                SpongeImpl.getLogger().error("Cannot load plugin '{}' from {} because it is missing the required dependencies {}",
                        failed.getId(), failed.getDisplaySource(), PluginReporter.formatRequirements(failed.getMissingRequirements()));
            }
        }

        return successfulCandidates;
    }

    private Optional<PluginContainer> loadPlugin(PluginCandidate candidate, ClassLoader classLoader, boolean addSourceToLaunchClassLoader) {
        final String id = candidate.getId();

        if (addSourceToLaunchClassLoader && candidate.getSource().isPresent()) {
            try {
                // Add JAR to classpath
                Launch.classLoader.addURL(candidate.getSource().get().toUri().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Failed to add plugin '" + id + "' from " + candidate.getDisplaySource() + " to classpath", e);
            }
        }

        final PluginMetadata metadata = candidate.getMetadata();
        final String name = firstNonNull(metadata.getName(), id);
        final String version = firstNonNull(metadata.getVersion(), "unknown");

        try {
            Class<?> pluginClass = classLoader.loadClass(candidate.getPluginClass());
            SpongeExtension ext = metadata.getExtension("sponge");
            PluginContainer container = new VanillaPluginContainer(id, pluginClass,
                    metadata.getName(), metadata.getVersion(), metadata.getDescription(), metadata.getUrl(), metadata.getAuthors(),
                    ext != null ? ext.getAssetDirectory() : null, candidate.getSource());

            registerPlugin(container);
            Sponge.getEventManager().registerListeners(container, container.getInstance().get());

            SpongeImpl.getLogger().info("Loaded plugin: {} {} (from {})", name, version, candidate.getDisplaySource());
            return Optional.of(container);
        } catch (Throwable e) {
            SpongeImpl.getLogger().error("Failed to load plugin: {} {} (from {})", name, version, candidate.getDisplaySource(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        checkNotNull(instance, "instance");

        if (instance instanceof PluginContainer) {
            return Optional.of((PluginContainer) instance);
        }

        return Optional.ofNullable(this.pluginInstances.get(instance));
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.ofNullable(this.plugins.get(id));
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return Collections.unmodifiableCollection(this.plugins.values());
    }

    @Override
    public boolean isLoaded(String id) {
        return this.plugins.containsKey(id);
    }

    
    // HotPluginManager
    
	@Override
	public HotPlugins loadPlugins(URLClassLoader classLoader) {
		PluginScanner pluginScanner = new PluginScanner();
		pluginScanner.scanClassPath(classLoader, true);
		Map<String, PluginCandidate> candidateMap = pluginScanner.getPlugins();
		Iterable<PluginContainer> hotPluginContainers = loadPlugins(candidateMap, classLoader, false);
		for (PluginContainer hotPluginContainer : hotPluginContainers) {
			post(GameStartingServerEvent.class, hotPluginContainer);
		}
		return new HotPluginsImpl(hotPluginContainers);
	}

	@Override
	public void unloadPlugins(HotPlugins hotPlugins) {
		HotPluginsImpl hotPluginsImpl = (HotPluginsImpl) hotPlugins;  
		Iterable<PluginContainer> hotPluginContainers = hotPluginsImpl.hotPluginContainers;
		for (PluginContainer hotPluginContainer : hotPluginContainers) {
			post(GameStoppingServerEvent.class, hotPluginContainer);
			hotPluginContainer.getInstance().ifPresent(p -> Sponge.getEventManager().unregisterPluginListeners(p));
			unregisterPlugin(hotPluginContainer);
		}
	}

	private void post(Class<? extends GameStateEvent> type, PluginContainer pluginContainer) {
		// do NOT getGame().setState(..)
		final GameStateEvent event = SpongeEventFactoryUtils.createState(type, Sponge.getGame());
		((SpongeEventManager)Sponge.getEventManager()).post(event, pluginContainer);
	}
	
	private static class HotPluginsImpl implements HotPlugins {
		private final Iterable<PluginContainer> hotPluginContainers;
		HotPluginsImpl(Iterable<PluginContainer> hotPluginContainers) {
			this.hotPluginContainers = hotPluginContainers;
		}
	}

}
