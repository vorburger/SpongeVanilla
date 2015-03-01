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

package org.granitepowered.granite.loader;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Throwables;
import org.granitepowered.granite.Granite;
import org.spongepowered.api.MinecraftVersion;

import java.io.File;
import java.util.Objects;

public class GraniteLoader {
    Mappings mappings;

    File minecraftJar;

    public static MinecraftVersion minecraftVersion;

    public void run() {
        try {
            downloadMinecraft();

            addMinecraftToClassPool();

            loadMappings();

            DeobfuscatorTransformer.init(this);

            applyTransformers();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void addMinecraftToClassPool() {
        MinecraftLoader.createPool(minecraftJar);
    }

    private void downloadMappings(File mappingsFile, String url, HttpRequest req) {
        Granite.getInstance().getLogger().warn("Downloading from " + url);
        if (req.code() == 404) {
            //throw new RuntimeException("GitHub 404 error whilst trying to download");
        } else if (req.code() == 200) {
            req.receive(mappingsFile);
            Granite.getInstance().getServerConfig().set("latest-mappings-etag", req.eTag());
            Granite.getInstance().getServerConfig().save();
        }
    }

    private void loadMappings() {
        File mappingsFile = new File(Granite.getInstance().getServerConfig().getMappingsFile().getAbsolutePath());
        String url = "https://raw.githubusercontent.com/GraniteTeam/GraniteMappings/sponge/1.8.3.json";
        try {
            HttpRequest req = HttpRequest.get(url);

            if (Granite.getInstance().getServerConfig().getAutomaticMappingsUpdating()) {
                Granite.getInstance().getLogger().info("Querying Granite for updates");
                if (!mappingsFile.exists()) {
                    Granite.getInstance().getLogger().warn("Could not find mappings.json");
                    downloadMappings(mappingsFile, url, req);
                } else if (!Objects.equals(req.eTag(), Granite.getInstance().getServerConfig().getLatestMappingsEtag())) {
                    Granite.getInstance().getLogger().info("Update found");
                    downloadMappings(mappingsFile, url, req);
                }
            }
        } catch (HttpRequest.HttpRequestException e) {
            Granite.getInstance().getLogger().warn("Could not reach Granite mappings, falling back to local");

            if (!mappingsFile.exists()) {
                Granite.getInstance().getLogger()
                        .warn("Could not find local mappings file. Obtain it (somehow) and place it in the server's root directory called \"mappings.json\"");
                Throwables.propagate(e);
            } else {
                Granite.error(e);
            }
        }

        mappings = MappingsLoader.load(mappingsFile);
    }

    private void applyTransformers() {
        Granite.getInstance().getLogger().info("Loading and deobfuscating Minecraft");

        String version = Granite.getInstance().getMinecraftVersion().getName().replace("Minecraft ", "");
        File jarFile = minecraftJar;
        File outputJarFile = new File("minecraft_server." + version + ".output.jar");

        MinecraftLoader loader = new MinecraftLoader();
        loader.load(jarFile, outputJarFile, new Mappings());
    }

    private void downloadMinecraft() {
        String version = "1.8.3";
        minecraftJar = new File("minecraft_server." + version + ".jar");

        if (!minecraftJar.exists()) {
            Granite.getInstance().getLogger().warn("Could not find Minecraft .jar, downloading");
            HttpRequest req = HttpRequest.get("https://s3.amazonaws.com/Minecraft.Download/versions/" + version + "/minecraft_server." + version + ".jar");
            if (req.code() == 404) {
                throw new RuntimeException("404 error whilst trying to download Minecraft");
            } else if (req.code() == 200) {
                req.receive(minecraftJar);
                Granite.getInstance().getLogger().info("Minecraft Downloaded");
            }
        }

        String minecraftVersion = minecraftJar.getName().replace("minecraft_server.", "Minecraft ").replace(".jar", "");
    }
}

