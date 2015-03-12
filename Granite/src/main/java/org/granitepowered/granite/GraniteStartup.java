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

package org.granitepowered.granite;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javassist.ClassPool;
import mc.Bootstrap;
import mc.DedicatedServer;
import mc.MinecraftServer;
import org.granitepowered.granite.guice.GraniteGuiceModule;
import org.granitepowered.granite.loader.DeobfuscatorTransformer;
import org.granitepowered.granite.loader.GraniteTweaker;
import org.granitepowered.granite.loader.Mappings;
import org.granitepowered.granite.loader.MappingsLoader;
import org.granitepowered.granite.loader.MinecraftLoader;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class GraniteStartup {

    String serverVersion;
    String apiVersion;
    String buildNumber = "UNKNOWN";

    Mappings mappings;

    File minecraftJar;

    public static void main(String[] args) {
        new GraniteStartup().run();
    }

    public void run() {
        Injector injector = Guice.createInjector(new GraniteGuiceModule());
        Granite.instance = injector.getInstance(Granite.class);
        Granite.getInstance().logger = LoggerFactory.getLogger("Granite");

        try {
            Properties versionProp = new Properties();
            InputStream versionIn = ClassLoader.getSystemResourceAsStream("version.properties");
            if (versionIn != null) {
                try {
                    versionProp.load(versionIn);

                    serverVersion = versionProp.getProperty("server", "UNKNOWN");
                    apiVersion = versionProp.getProperty("api", "UNKNOWN");

                    String build = versionProp.getProperty("build");
                    if (build != null && !build.equals("NA")) {
                        buildNumber = build;
                    }
                } catch (IOException ignored) {
                } finally {
                    try {
                        versionIn.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            Granite.getInstance().getLogger()
                    .info("Starting Granite version " + serverVersion + " build " + buildNumber + " implementing API version " + apiVersion);

            Granite.getInstance().version = serverVersion;
            Granite.getInstance().apiVersion = apiVersion;

            Granite.getInstance().classPool = ClassPool.getDefault();

            Granite.getInstance().serverConfig = new ServerConfig();

            loadMinecraft();

            loadMappings();

            DeobfuscatorTransformer.mappings = mappings;
            DeobfuscatorTransformer.minecraftJar = minecraftJar;
            DeobfuscatorTransformer.init();

            bootstrap();

            injectSpongeFields();

            Date date = new Date();
            String day = new SimpleDateFormat("dd").format(date);
            String month = new SimpleDateFormat("MM").format(date);
            String year = new SimpleDateFormat("yyyy").format(date);
            if (Objects.equals(day + month, "0101")) {
                Granite.getInstance().getLogger().info("HAPPY NEW YEAR!");
            }
            if (Objects.equals(day + month, "2208")) {
                Granite.getInstance().getLogger().info("Happy Birthday Voltasalt!");
            }
            if (Objects.equals(day + month, "0709")) {
                String start = "2014";
                Granite.getInstance().getLogger()
                        .info("Happy Birthday Granite! Granite is " + Integer.toString(Integer.parseInt(year) - Integer.parseInt(start)) + " today!");
            }
            if (Objects.equals(day + month, "2310")) {
                Granite.getInstance().getLogger().info("Happy Birthday AzureusNation!");
            }
            if (Objects.equals(day + month, "3110")) {
                Granite.getInstance().getLogger().info("Happy Halloween!");
            }
            if (Objects.equals(day + month, "2412")) {
                Granite.getInstance().getLogger().info("Santa is getting ready!");
            }
            if (Objects.equals(day + month, "2512")) {
                Granite.getInstance().getLogger().info("Merry Christmas/Happy Holidays!");
            }
            if (Objects.equals(day + month, "3112")) {
                Granite.getInstance().getLogger().info("New Years Eve. Make way for " + Integer.toString(Integer.parseInt(year) + 1) + "!");
            }

            Granite.instance.server = (Server) new DedicatedServer(new File("worlds/"));

            // Start the server
            Thread t = new Thread((MinecraftServer) Granite.instance.server);
            t.start();
        } catch (Throwable t) {
            Granite.error("We did a REALLY BIG boo-boo :'(", t);
        }
    }

    private void injectSpongeFields() {
        Granite.getInstance().getLogger().info("Injecting Sponge fields");

        /*injectConstant(Messages.class, "factory", new GraniteMessageFactory());
        injectConstant(TextStyles.class, "factory", new GraniteTextFormatFactory());
        injectConstant(TextActions.class, "factory", new GraniteTextActionFactory());
        injectConstant(Translations.class, "factory", new GraniteTranslationFactory());
        injectConstant(ChatTypes.class, "factory", new GraniteChatTypeFactory());
        injectConstant(Titles.class, "factory", new GraniteTitleFactory());

        Map<String, TextStyle.Base> styles = new HashMap<>();
        for (Map.Entry<String, TextStyle.Base> entry : GraniteTextFormatFactory.styles.entrySet()) {
            styles.put(entry.getKey().toUpperCase(), entry.getValue());
        }

        injectConstants(TextStyles.class, styles);*/
    }

    private void injectEnumConstants(Class<?> destination, Class<? extends Enum> source) {
        for (Enum constant : source.getEnumConstants()) {
            injectConstant(destination, constant.name(), constant);
        }
    }

    private void injectConstants(Class<?> clazz, Map<String, ?> objects) {
        for (Map.Entry<String, ?> entry : objects.entrySet()) {
            injectConstant(clazz, entry.getKey(), entry.getValue());
        }
    }

    private void injectConstant(Class<?> clazz, String name, Object value) {
        try {
            Field f = clazz.getDeclaredField(name);
            try {
                f.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Throwables.propagate(e);
            }

            f.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Throwables.propagate(e);
        }
    }

    private void bootstrap() {
        Granite.getInstance().getLogger().info("Bootstrapping Minecraft");

        Bootstrap.register();
    }

    private void downloadMappings(File mappingsFile, String url, HttpRequest req) {
        Granite.getInstance().getLogger().warn("Downloading mappings from " + url);
        if (req.code() == 404) {
            throw new RuntimeException("GitHub 404 error whilst trying to download mappings");
        } else if (req.code() == 200) {
            req.receive(mappingsFile);
            Granite.getInstance().getServerConfig().set("latest-mappings-etag", req.eTag());
            Granite.getInstance().getServerConfig().save();
        }
    }

    private void loadMappings() {
        File mappingsFile = new File(Granite.getInstance().getServerConfig().getMappingsFile().getAbsolutePath());
        String url = "https://raw.githubusercontent.com/GraniteTeam/GraniteMappings/mixin/1.8.3.json";
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

    private void loadMinecraft() {
        String version = "1.8.3";
        minecraftJar = new File("minecraft_server." + version + ".jar");

        if (!minecraftJar.exists()) {
            Granite.getInstance().getLogger().warn("Could not find Minecraft .jar, downloading");
            HttpRequest
                    req =
                    HttpRequest.get("https://s3.amazonaws.com/Minecraft.Download/versions/" + version + "/minecraft_server." + version + ".jar");
            if (req.code() == 404) {
                throw new RuntimeException("404 error whilst trying to download Minecraft");
            } else if (req.code() == 200) {
                req.receive(minecraftJar);
                Granite.getInstance().getLogger().info("Minecraft Downloaded");
            }
        }

        String minecraftVersion = minecraftJar.getName().replace("minecraft_server.", "Minecraft ").replace(".jar", "");

        MinecraftLoader.createPool(minecraftJar);
        try {
            GraniteTweaker.loader.addURL(minecraftJar.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
