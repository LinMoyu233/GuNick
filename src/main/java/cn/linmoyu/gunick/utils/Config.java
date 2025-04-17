package cn.linmoyu.gunick.utils;

import cn.linmoyu.gunick.GuNick;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.regex.Pattern;

public class Config {

    public static String mysql_host;
    public static int mysql_port;
    public static String mysql_database;
    public static String mysql_user;
    public static String mysql_password;
    public static boolean mysql_ssl;

    public static boolean isLobby;
    public static String lobbyMode;
    public static boolean forceNickCommandOnGame;

    public static boolean bookGui;

    public static boolean actionbar_enable;
    public static boolean actionbar_vanishbar;

    public static int nickMinLength = 3;
    public static int nickLength = 16;
    public static Pattern namePattern = Pattern.compile("[\\u4e00-\\u9fa5_a-zA-Z0-9]*");

    public static void setupConfig(Plugin plugin) {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();
        mysql_host = config.getString("database.host");
        mysql_port = config.getInt("database.port");
        mysql_database = config.getString("database.database");
        mysql_user = config.getString("database.user");
        mysql_password = config.getString("database.password");
        mysql_ssl = config.getBoolean("database.ssl");

        lobbyMode = config.getString("lobbyMode");
        if (lobbyMode.equalsIgnoreCase("true") || lobbyMode.equalsIgnoreCase("autodetect")) {
            detectLobby();
        }
        forceNickCommandOnGame = config.getBoolean("forceNickCommandOnGame.enable");

        bookGui = config.getBoolean("bookGui");

        actionbar_enable = config.getBoolean("actionbar.enable");
        actionbar_vanishbar = config.getBoolean("actionbar.vanishbar");

        Messages.loadMessages(config);
    }

    public static void reloadConfig(Plugin plugin) {
        plugin.reloadConfig();
        setupConfig(plugin);

        GuNick.getRemoteDatabase().close();
        GuNick.getPlugin().connectDatabase();
    }

    public static void detectLobby() {
        if (lobbyMode.equalsIgnoreCase("true")) {
            isLobby = true;
            return;
        }
        PluginManager pm = Bukkit.getPluginManager();
        if (pm.getPlugin("SuperLobby") != null || pm.getPlugin("DeluxeHub") != null || pm.getPlugin("Akropolis") != null) {
            isLobby = true;
        }
    }
}
