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

package org.granitepowered.granite.impl.plugin;

import com.google.common.base.Optional;
import com.google.inject.Injector;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.granitepowered.granite.Granite;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.inject.Inject;

public class GranitePluginManager implements PluginManager {

    private final Injector injector;
    Collection<PluginContainer> plugins = new ArrayList<>();

    @Inject
    public GranitePluginManager(Injector injector) {
        this.injector = injector;
    }

    public void loadPlugins() {
        File[] files = Granite.getInstance().getServerConfig().getPluginDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File arg0, String arg1) {
                return arg1.endsWith(".jar");
            }
        });

        if (files != null) {
            ArrayList<PluginContainer> pluginContainers = new ArrayList<PluginContainer>();

            for (File plugin : files) {
                Granite.getInstance().getLogger().info("Loading jarfile plugins/{}", plugin.getName());

                try {
                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{plugin.toURI().toURL()});
                    ClassPool classPool = new ClassPool(true);
                    classPool.appendClassPath(plugin.getAbsolutePath());

                    JarFile jarFile = new JarFile(plugin);

                    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();

                    while (jarEntryEnumeration.hasMoreElements()) {
                        JarEntry jarEntry = jarEntryEnumeration.nextElement();

                        if (jarEntry.getName().endsWith(".class")) {
                            String className = jarEntry.getName().replaceAll("/", ".").substring(0, jarEntry.getName().length() - ".class".length());

                            try {
                                CtClass ctClass = classPool.get(className);

                                if (ctClass.hasAnnotation(Plugin.class)) {
                                    PluginContainer pluginContainer = new GranitePluginContainer(injector, classLoader.loadClass(className));
                                    pluginContainers.add(pluginContainer);
                                }
                            } catch (ClassNotFoundException | NotFoundException e) {
                                Granite.error(e);
                            }

                        }
                    }
                } catch (IOException | NotFoundException e) {
                    Granite.error(e);
                }
            }

            for (PluginContainer plugin : pluginContainers) {
                ArrayList<String> missingDependencies = new ArrayList<>();

                String[] dependencies = ((GranitePluginContainer) plugin).getDependencies().split(";");
                for (String s : dependencies) {
                    String[] args = s.split(":");
                    if (args.length != 2) {
                        continue;
                    }

                    boolean required = false;
                    String pluginName = args[1];

                    if (args[0].startsWith("required-")) {
                        required = true;
                    }

                    for (PluginContainer p : pluginContainers) {
                        if (!p.getId().equals(pluginName) && required) {
                            missingDependencies.add(pluginName);
                        }
                    }
                }

                if (missingDependencies.size() == 0) {
                    plugins.add(plugin);
                    Granite.getInstance().getEventManager().register(plugin.getInstance(), plugin.getInstance());
                    Granite.getInstance().getLogger().info("Loaded {} ({})!", plugin.getName(), plugin.getVersion());
                } else {
                    Granite.getInstance().getLogger().info("Could not load {} ({})! Missing dependencies: {}", plugin.getName(), plugin.getVersion(),
                            missingDependencies.toString());
                }
            }
        }
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        for (PluginContainer pluginContainer : plugins) {
            if (pluginContainer.getInstance().equals(instance)) {
                return Optional.of(pluginContainer);
            }
        }
        return Optional.absent();
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        for (PluginContainer pluginContainer : plugins) {
            if (pluginContainer.getId().equals(id)) {
                return Optional.of(pluginContainer);
            }
        }
        return Optional.absent();
    }

    @Override
    public Logger getLogger(PluginContainer plugin) {
        return ((GranitePluginContainer) plugin).getLogger();
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return plugins;
    }

    @Override
    public boolean isLoaded(String id) {
        for (PluginContainer pluginContainer : plugins) {
            if (pluginContainer.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
