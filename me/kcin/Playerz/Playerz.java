/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package me.kcin.Playerz;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import me.kcin.Playerz.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Playerz
extends JavaPlugin {
    public static Playerz plugin;
    public final Logger logger = Logger.getLogger("Minecraft");
    public static int tid;
    public static int running;
    static FileConfiguration config;

    static {
        tid = 0;
        running = 1;
    }

    public void onEnable() {
        try {
            config = this.getConfig();
            this.getDataFolder().mkdir();
            File Config = new File(this.getDataFolder() + File.separator + "config.yml");
            Config.createNewFile();
            if (!config.contains("Permissions.Enabled.List")) {
                config.set("Permissions.Enabled.List", (Object)false);
            }
            if (!config.contains("Permissions.Enabled.Toggle")) {
                config.set("Permissions.Enabled.Toggle", (Object)true);
            }
            if (!config.contains("Permissions.OpOverride")) {
                config.set("Permissions.OpOverride", (Object)true);
            }
            if (!config.contains("Permissions.Denied")) {
                config.set("Permissions.Denied", (Object)"&4[Playerz] &fYou aren't allowed to use this command.");
            }
            if (!config.contains("List.NumberOfGroups")) {
                config.set("List.NumberOfGroups", (Object)1);
            }
            if (!config.contains("List.DisplayPrefix")) {
                config.set("List.DisplayPrefix", (Object)true);
            }
            if (!config.contains("List.DisplaySuffix")) {
                config.set("List.DisplaySuffix", (Object)true);
            }
            if (!config.contains("List.Prefix")) {
                config.set("List.Prefix", (Object)"&6----------------------[&ePlayerz&6]-----------------------");
            }
            if (!config.contains("List.PlayerAmount")) {
                config.set("List.PlayerAmount", (Object)"&3There are (&6%ONLINEPLAYERS%&3/&6%MAXPLAYERS%&3) players online.");
            }
            int ga = config.getInt("List.NumberOfGroups");
            int cc = 1;
            while (cc < ga + 1) {
                if (!config.contains("List." + cc)) {
                    config.set("List." + cc, (Object)("&3Group " + cc + ": &6%GROUP" + cc + "%"));
                }
                ++cc;
            }
            if (!config.contains("List.Suffix")) {
                config.set("List.Suffix", (Object)"&6-----------------------------------------------------");
            }
            if (!config.contains("Broadcast.RunOnStart")) {
                config.set("Broadcast.RunOnStart", (Object)false);
            }
            if (!config.contains("Broadcast.Interval")) {
                config.set("Broadcast.Interval", (Object)300);
            }
            if (!config.contains("Broadcast.DisplayPrefix")) {
                config.set("Broadcast.DisplayPrefix", (Object)true);
            }
            if (!config.contains("Broadcast.DisplaySuffix")) {
                config.set("Broadcast.DisplaySuffix", (Object)true);
            }
            if (!config.contains("Broadcast.DisplayPlayerAmount")) {
                config.set("Broadcast.DisplayPlayerAmount", (Object)true);
            }
            if (!config.contains("Broadcast.DisplayGroups")) {
                config.set("Broadcast.DisplayGroups", (Object)true);
            }
            if (!config.contains("Broadcast.Start")) {
                config.set("Broadcast.Start", (Object)"&4[Playerz] &fAutomatic list broadcasts started.");
            }
            if (!config.contains("Broadcast.Stop")) {
                config.set("Broadcast.Stop", (Object)"&4[Playerz] &fAutomatic list broadcasts stopped.");
            }
            this.saveConfig();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        if (config.getBoolean("Broadcast.RunOnStart")) {
            tid = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable(){

                @Override
                public void run() {
                    try {
                        Playerz.broadcastMessage();
                    }
                    catch (IOException var1_1) {
                        // empty catch block
                    }
                }
            }, 0, (long)(config.getInt("Broadcast.Interval") * 20));
        }
        try {
            Metrics metrics = new Metrics();
            metrics.beginMeasuringPlugin((Plugin)this);
        }
        catch (IOException metrics) {
            // empty catch block
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player)sender;
        }
        if (commandLabel.equalsIgnoreCase("list") || commandLabel.equalsIgnoreCase("who") || commandLabel.equalsIgnoreCase("players")) {
            if (config.getBoolean("Permissions.Enabled.List")) {
                if (player == null || player.hasPermission("playerz.list") || config.getBoolean("Permissions.OpOverride") && (player == null || player.isOp())) {
                    int maxplayers = this.getServer().getMaxPlayers();
                    Player[] names = this.getServer().getOnlinePlayers();
                    if (config.getBoolean("List.DisplayPrefix")) {
                        if (player == null) {
                            this.logger.info(this.noColors(config.getString("List.Prefix")));
                        } else {
                            player.sendMessage(Playerz.addColors(config.getString("List.Prefix")));
                        }
                    }
                    String playeramount1 = Playerz.StrReplace("%ONLINEPLAYERS%", String.valueOf(names.length), config.getString("List.PlayerAmount"));
                    String playeramount2 = Playerz.StrReplace("%MAXPLAYERS%", String.valueOf(maxplayers), playeramount1);
                    if (player == null) {
                        this.logger.info(this.noColors(playeramount2));
                    } else {
                        player.sendMessage(Playerz.addColors(playeramount2));
                    }
                    Player[] aplayer1 = sender.getServer().getOnlinePlayers();
                    int l = aplayer1.length;
                    int pp = config.getInt("List.NumberOfGroups");
                    String[] sentencez = new String[99];
                    int j = 0;
                    while (j < l) {
                        Player p = aplayer1[j];
                        int ppc = 1;
                        while (ppc < pp + 1) {
                            if (p.hasPermission("playerz." + ppc)) {
                                sentencez[ppc] = sentencez[ppc] == null ? String.valueOf(p.getName()) + ", " : String.valueOf(sentencez[ppc]) + p.getName() + ", ";
                            }
                            ++ppc;
                        }
                        ++j;
                    }
                    int ppc = 1;
                    while (ppc < pp + 1) {
                        if (player == null && sentencez[ppc] != null) {
                            this.logger.info(this.noColors(Playerz.StrReplace("%GROUP" + ppc + "%", sentencez[ppc].toString(), config.getString("List." + ppc))));
                        } else if (player != null && sentencez[ppc] != null) {
                            player.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc + "%", sentencez[ppc].toString(), config.getString("List." + ppc))));
                        } else if (player == null && sentencez[ppc] == null) {
                            this.logger.info(this.noColors(Playerz.StrReplace("%GROUP" + ppc + "%", "none", config.getString("List." + ppc))));
                        } else if (player != null && sentencez[ppc] == null) {
                            player.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc + "%", "none", config.getString("List." + ppc))));
                        }
                        ++ppc;
                    }
                    if (config.getBoolean("List.DisplaySuffix")) {
                        if (player == null) {
                            this.logger.info(this.noColors(config.getString("List.Suffix")));
                        } else {
                            player.sendMessage(Playerz.addColors(config.getString("List.Suffix")));
                        }
                    }
                } else {
                    player.sendMessage(Playerz.addColors(config.getString("Permissions.Denied")));
                }
            } else {
                int maxplayers = this.getServer().getMaxPlayers();
                Player[] names = this.getServer().getOnlinePlayers();
                if (config.getBoolean("List.DisplayPrefix")) {
                    if (player == null) {
                        this.logger.info(this.noColors(config.getString("List.Prefix")));
                    } else {
                        player.sendMessage(Playerz.addColors(config.getString("List.Prefix")));
                    }
                }
                String playeramount1 = Playerz.StrReplace("%ONLINEPLAYERS%", String.valueOf(names.length), config.getString("List.PlayerAmount"));
                String playeramount2 = Playerz.StrReplace("%MAXPLAYERS%", String.valueOf(maxplayers), playeramount1);
                if (player == null) {
                    this.logger.info(this.noColors(playeramount2));
                } else {
                    player.sendMessage(Playerz.addColors(playeramount2));
                }
                Player[] aplayer1 = sender.getServer().getOnlinePlayers();
                int l = aplayer1.length;
                int pp = config.getInt("List.NumberOfGroups");
                String[] sentencez = new String[99];
                int j = 0;
                while (j < l) {
                    Player p = aplayer1[j];
                    int ppc = 1;
                    while (ppc < pp + 1) {
                        if (p.hasPermission("playerz." + ppc)) {
                            sentencez[ppc] = sentencez[ppc] == null ? String.valueOf(p.getName()) + ", " : String.valueOf(sentencez[ppc]) + p.getName() + ", ";
                        }
                        ++ppc;
                    }
                    ++j;
                }
                int ppc = 1;
                while (ppc < pp + 1) {
                    if (player == null && sentencez[ppc] != null) {
                        this.logger.info(this.noColors(Playerz.StrReplace("%GROUP" + ppc + "%", sentencez[ppc].toString(), config.getString("List." + ppc))));
                    } else if (player != null && sentencez[ppc] != null) {
                        player.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc + "%", sentencez[ppc].toString(), config.getString("List." + ppc))));
                    } else if (player == null && sentencez[ppc] == null) {
                        this.logger.info(this.noColors(Playerz.StrReplace("%GROUP" + ppc + "%", "none", config.getString("List." + ppc))));
                    } else if (player != null && sentencez[ppc] == null) {
                        player.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc + "%", "none", config.getString("List." + ppc))));
                    }
                    ++ppc;
                }
                if (config.getBoolean("List.DisplaySuffix")) {
                    if (player == null) {
                        this.logger.info(this.noColors(config.getString("List.Suffix")));
                    } else {
                        player.sendMessage(Playerz.addColors(config.getString("List.Suffix")));
                    }
                }
            }
        } else if (commandLabel.equalsIgnoreCase("playerz")) {
            if (config.getBoolean("Permissions.Enabled.Toggle")) {
                if (player == null || player.hasPermission("playerz.toggle") || player == null || config.getBoolean("Permissions.OpOverride") && player.isOp()) {
                    if (running == 1) {
                        Bukkit.getServer().getScheduler().cancelTask(tid);
                        running = 0;
                        if (player == null) {
                            this.logger.info(this.noColors(config.getString("Broadcast.Stop")));
                        } else {
                            player.sendMessage(Playerz.addColors(config.getString("Broadcast.Stop")));
                        }
                    } else {
                        tid = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable(){

                            @Override
                            public void run() {
                                try {
                                    Playerz.broadcastMessage();
                                }
                                catch (IOException var1_1) {
                                    // empty catch block
                                }
                            }
                        }, 0, (long)(config.getInt("Broadcast.Interval") * 20));
                        running = 1;
                        if (player == null) {
                            this.logger.info(this.noColors(config.getString("Broadcast.Start")));
                        } else {
                            player.sendMessage(Playerz.addColors(config.getString("Broadcast.Start")));
                        }
                    }
                } else {
                    player.sendMessage(Playerz.addColors(config.getString("Permissions.Denied")));
                }
            } else if (running == 1) {
                Bukkit.getServer().getScheduler().cancelTask(tid);
                running = 0;
                if (player == null) {
                    this.logger.info(this.noColors(config.getString("Broadcast.Stop")));
                } else {
                    player.sendMessage(Playerz.addColors(config.getString("Broadcast.Stop")));
                }
            } else if (running == 0) {
                tid = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable(){

                    @Override
                    public void run() {
                        try {
                            Playerz.broadcastMessage();
                        }
                        catch (IOException var1_1) {
                            // empty catch block
                        }
                    }
                }, 0, (long)(config.getInt("Broadcast.Interval") * 20));
                running = 1;
                if (player == null) {
                    this.logger.info(this.noColors(config.getString("Broadcast.Start")));
                } else {
                    player.sendMessage(Playerz.addColors(config.getString("Broadcast.Start")));
                }
            } else if (running == 2) {
                if (player == null) {
                    this.logger.info(this.noColors(config.getString("Broadcast.AllFalse")));
                } else {
                    player.sendMessage(Playerz.addColors(config.getString("Broadcast.AllFalse")));
                }
            }
        }
        return false;
    }

    public static void broadcastMessage() throws IOException {
        int l;
        Player[] arrplayer;
        Player playah;
        Player[] sentencez;
        int pp;
        int n;
        int n2;
        int maxplayers = Bukkit.getServer().getMaxPlayers();
        Player[] names = Bukkit.getServer().getOnlinePlayers();
        if (config.getBoolean("Broadcast.DisplayPrefix")) {
            arrplayer = Bukkit.getServer().getOnlinePlayers();
            n = arrplayer.length;
            n2 = 0;
            while (n2 < n) {
                playah = arrplayer[n2];
                if (config.getBoolean("Permissions.Enabled.List")) {
                    if (playah.hasPermission("playerz.list")) {
                        playah.sendMessage(Playerz.addColors(config.getString("List.Prefix")));
                    }
                } else {
                    playah.sendMessage(Playerz.addColors(config.getString("List.Prefix")));
                }
                ++n2;
            }
        }
        if (config.getBoolean("Broadcast.DisplayPlayerAmount")) {
            arrplayer = Bukkit.getServer().getOnlinePlayers();
            n = arrplayer.length;
            n2 = 0;
            while (n2 < n) {
                playah = arrplayer[n2];
                if (config.getBoolean("Permissions.Enabled.List")) {
                    if (playah.hasPermission("playerz.list")) {
                        playah.sendMessage(Playerz.addColors(Playerz.StrReplace("%MAXPLAYERS%", String.valueOf(maxplayers), Playerz.StrReplace("%ONLINEPLAYERS%", String.valueOf(names.length), config.getString("List.PlayerAmount")))));
                    }
                } else {
                    playah.sendMessage(Playerz.addColors(Playerz.StrReplace("%MAXPLAYERS%", String.valueOf(maxplayers), Playerz.StrReplace("%ONLINEPLAYERS%", String.valueOf(names.length), config.getString("List.PlayerAmount")))));
                }
                ++n2;
            }
        }
        if (config.getBoolean("Broadcast.DisplayGroups")) {
            int ppc;
            Player[] aplayer1 = Bukkit.getServer().getOnlinePlayers();
            l = aplayer1.length;
            pp = config.getInt("List.NumberOfGroups");
            sentencez = new String[99];
            int j = 0;
            while (j < l) {
                Player p = aplayer1[j];
                ppc = 1;
                while (ppc < pp + 1) {
                    if (p.hasPermission("playerz." + ppc)) {
                        sentencez[ppc] = sentencez[ppc] == null ? String.valueOf(p.getName()) + ", " : String.valueOf((Object)sentencez[ppc]) + p.getName() + ", ";
                    }
                    ++ppc;
                }
                ++j;
            }
            int ppc2 = 1;
            while (ppc2 < pp + 1) {
                Player[] arrplayer2;
                Player playah2;
                int n3;
                if (sentencez[ppc2] == null) {
                    arrplayer2 = Bukkit.getServer().getOnlinePlayers();
                    n3 = arrplayer2.length;
                    ppc = 0;
                    while (ppc < n3) {
                        playah2 = arrplayer2[ppc];
                        if (config.getBoolean("Permissions.Enabled.List")) {
                            if (playah2.hasPermission("playerz.list")) {
                                playah2.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc2 + "%", "none", config.getString("List." + ppc2))));
                            }
                        } else {
                            playah2.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc2 + "%", "none", config.getString("List." + ppc2))));
                        }
                        ++ppc;
                    }
                } else {
                    arrplayer2 = Bukkit.getServer().getOnlinePlayers();
                    n3 = arrplayer2.length;
                    ppc = 0;
                    while (ppc < n3) {
                        playah2 = arrplayer2[ppc];
                        if (config.getBoolean("Permissions.Enabled.List")) {
                            if (playah2.hasPermission("playerz.list")) {
                                playah2.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc2 + "%", sentencez[ppc2].toString(), config.getString("List." + ppc2))));
                            }
                        } else {
                            playah2.sendMessage(Playerz.addColors(Playerz.StrReplace("%GROUP" + ppc2 + "%", sentencez[ppc2].toString(), config.getString("List." + ppc2))));
                        }
                        ++ppc;
                    }
                }
                ++ppc2;
            }
        }
        if (config.getBoolean("Broadcast.DisplaySuffix")) {
            sentencez = Bukkit.getServer().getOnlinePlayers();
            pp = sentencez.length;
            l = 0;
            while (l < pp) {
                playah = sentencez[l];
                if (config.getBoolean("Permissions.Enabled.List")) {
                    if (playah.hasPermission("playerz.list")) {
                        playah.sendMessage(Playerz.addColors(config.getString("List.Suffix")));
                    }
                } else {
                    playah.sendMessage(Playerz.addColors(config.getString("List.Suffix")));
                }
                ++l;
            }
        }
    }

    public static String addColors(String string) {
        string = string.replaceAll("&0", (String)((Object)ChatColor.BLACK));
        string = string.replaceAll("&1", (String)((Object)ChatColor.DARK_BLUE));
        string = string.replaceAll("&2", (String)((Object)ChatColor.DARK_GREEN));
        string = string.replaceAll("&3", (String)((Object)ChatColor.DARK_AQUA));
        string = string.replaceAll("&4", (String)((Object)ChatColor.DARK_RED));
        string = string.replaceAll("&5", (String)((Object)ChatColor.DARK_PURPLE));
        string = string.replaceAll("&6", (String)((Object)ChatColor.GOLD));
        string = string.replaceAll("&7", (String)((Object)ChatColor.GRAY));
        string = string.replaceAll("&8", (String)((Object)ChatColor.DARK_GRAY));
        string = string.replaceAll("&9", (String)((Object)ChatColor.BLUE));
        string = string.replaceAll("&a", (String)((Object)ChatColor.GREEN));
        string = string.replaceAll("&b", (String)((Object)ChatColor.AQUA));
        string = string.replaceAll("&c", (String)((Object)ChatColor.RED));
        string = string.replaceAll("&d", (String)((Object)ChatColor.LIGHT_PURPLE));
        string = string.replaceAll("&e", (String)((Object)ChatColor.YELLOW));
        string = string.replaceAll("&f", (String)((Object)ChatColor.WHITE));
        return string;
    }

    public String noColors(String string) {
        string = string.replaceAll("&0", "");
        string = string.replaceAll("&1", "");
        string = string.replaceAll("&2", "");
        string = string.replaceAll("&3", "");
        string = string.replaceAll("&4", "");
        string = string.replaceAll("&5", "");
        string = string.replaceAll("&6", "");
        string = string.replaceAll("&7", "");
        string = string.replaceAll("&8", "");
        string = string.replaceAll("&9", "");
        string = string.replaceAll("&a", "");
        string = string.replaceAll("&b", "");
        string = string.replaceAll("&c", "");
        string = string.replaceAll("&d", "");
        string = string.replaceAll("&e", "");
        string = string.replaceAll("&f", "");
        return string;
    }

    public static String StrReplace(String search, String replace, String subject) {
        StringBuffer result = new StringBuffer(subject);
        int pos = 0;
        while ((pos = result.indexOf(search, pos)) != -1) {
            result.replace(pos, pos + search.length(), replace);
        }
        return result.toString();
    }

}

