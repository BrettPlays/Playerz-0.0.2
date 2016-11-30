/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.configuration.file.YamlConfigurationOptions
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.scheduler.BukkitScheduler
 */
package me.kcin.Playerz;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;

public class Metrics {
    private static final int REVISION = 4;
    private static final String BASE_URL = "http://metrics.griefcraft.com";
    private static final String REPORT_URL = "/report/%s";
    private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";
    private static final int PING_INTERVAL = 10;
    private Map<Plugin, Set<Plotter>> customData = Collections.synchronizedMap(new HashMap());
    private final YamlConfiguration configuration;
    private String guid;

    public Metrics() throws IOException {
        File file = new File("plugins/PluginMetrics/config.yml");
        this.configuration = YamlConfiguration.loadConfiguration((File)file);
        this.configuration.addDefault("opt-out", (Object)false);
        this.configuration.addDefault("guid", (Object)UUID.randomUUID().toString());
        if (this.configuration.get("guid", (Object)null) == null) {
            this.configuration.options().header("http://metrics.griefcraft.com").copyDefaults(true);
            this.configuration.save(file);
        }
        this.guid = this.configuration.getString("guid");
    }

    public void addCustomData(Plugin plugin, Plotter plotter) {
        Set plotters = this.customData.get((Object)plugin);
        if (plotters == null) {
            plotters = Collections.synchronizedSet(new LinkedHashSet());
            this.customData.put(plugin, plotters);
        }
        plotters.add(plotter);
    }

    public void beginMeasuringPlugin(final Plugin plugin) throws IOException {
        if (this.configuration.getBoolean("opt-out", false)) {
            return;
        }
        this.postPlugin(plugin, false);
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable(){

            @Override
            public void run() {
                try {
                    Metrics.this.postPlugin(plugin, true);
                }
                catch (IOException e) {
                    System.out.println("[Metrics] " + e.getMessage());
                }
            }
        }, 12000, 12000);
    }

    private void postPlugin(Plugin plugin, boolean isPing) throws IOException {
        Set<Plotter> plotters;
        String response = "ERR No response";
        String data = String.valueOf(Metrics.encode("guid")) + '=' + Metrics.encode(this.guid) + '&' + Metrics.encode("version") + '=' + Metrics.encode(plugin.getDescription().getVersion()) + '&' + Metrics.encode("server") + '=' + Metrics.encode(Bukkit.getVersion()) + '&' + Metrics.encode("players") + '=' + Metrics.encode(String.valueOf(Bukkit.getServer().getOnlinePlayers().length)) + '&' + Metrics.encode("revision") + '=' + Metrics.encode("4");
        if (isPing) {
            data = String.valueOf(data) + '&' + Metrics.encode("ping") + '=' + Metrics.encode("true");
        }
        if ((plotters = this.customData.get((Object)plugin)) != null) {
            for (Plotter plotter : plotters) {
                data = String.valueOf(data) + "&" + Metrics.encode(new StringBuilder("Custom").append(plotter.getColumnName()).toString()) + "=" + Metrics.encode(Integer.toString(plotter.getValue()));
            }
        }
        URL url = new URL("http://metrics.griefcraft.com" + String.format("/report/%s", plugin.getDescription().getName()));
        URLConnection connection = this.isMineshafterPresent() ? url.openConnection(Proxy.NO_PROXY) : url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = reader.readLine();
        writer.close();
        reader.close();
        if (response.startsWith("ERR")) {
            throw new IOException(response);
        }
        if (response.contains("OK This is your first update this hour") && plotters != null) {
            for (Plotter plotter : plotters) {
                plotter.reset();
            }
        }
    }

    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    public static abstract class Plotter {
        public abstract String getColumnName();

        public abstract int getValue();

        public void reset() {
        }

        public int hashCode() {
            return this.getColumnName().hashCode() + this.getValue();
        }

        public boolean equals(Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }
            Plotter plotter = (Plotter)object;
            if (plotter.getColumnName().equals(this.getColumnName()) && plotter.getValue() == this.getValue()) {
                return true;
            }
            return false;
        }
    }

}

