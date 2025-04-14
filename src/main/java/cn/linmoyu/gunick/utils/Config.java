package cn.linmoyu.gunick.utils;

import cn.linmoyu.gunick.GuNick;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    public static String mysql_host;
    public static int mysql_port;
    public static String mysql_database;
    public static String mysql_user;
    public static String mysql_password;
    public static boolean mysql_ssl;
    public static boolean isLobby;
    public static int nickMinLength = 3;
    public static int nickLength = 16;
    public static String nickAllowedChar = "[\\u4e00-\\u9fa5_a-zA-Z0-9]*";

    public static void setupConfig(Plugin plugin) {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();
        mysql_host = config.getString("database.host");
        mysql_port = config.getInt("database.port");
        mysql_database = config.getString("database.database");
        mysql_user = config.getString("database.user");
        mysql_password = config.getString("database.password");
        mysql_ssl = config.getBoolean("database.ssl");
    }

    public static void reloadConfig(Plugin plugin) {
        plugin.reloadConfig();

        GuNick.getRemoteDatabase().close();
        GuNick.getPlugin().connectDatabase();
    }
}
