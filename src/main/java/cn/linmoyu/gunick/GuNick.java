package cn.linmoyu.gunick;

import cn.linmoyu.gunick.command.NickBookGUICommand;
import cn.linmoyu.gunick.command.NickCommand;
import cn.linmoyu.gunick.command.UnNickCommand;
import cn.linmoyu.gunick.database.Database;
import cn.linmoyu.gunick.database.MySQL;
import cn.linmoyu.gunick.database.PlayerData;
import cn.linmoyu.gunick.listener.*;
import cn.linmoyu.gunick.nms.VersionSupport;
import cn.linmoyu.gunick.nms.v1_8;
import cn.linmoyu.gunick.utils.Config;
import cn.linmoyu.gunick.utils.Messages;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class GuNick extends JavaPlugin implements Listener {
    @Getter
    private static GuNick plugin;
    @Getter
    private static VersionSupport versionSupport;
    @Getter
    private static Database remoteDatabase;
    @Getter
    private final String aboutMessage = Messages.translateCC("§f* This server is running §bGuNick Plugin§f. \n§f* By §b@YukiEnd §f| §bLinMoyu_ §7v" + getDescription().getVersion());
    @Getter
    private DisguiseProvider disguiseProvider = DisguiseManager.getProvider();
    @Getter
    private ConcurrentHashMap<UUID, PlayerData> dataCache = new ConcurrentHashMap<>();

    public GuNick() {
        disguiseProvider.allowOverrideChat(false);
        disguiseProvider.setNameLength(Config.nickLength);
        disguiseProvider.setNamePattern(Config.namePattern);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Config.setupConfig(this);

        connectDatabase();

        versionSupport = new v1_8();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerNickListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerUnNickListener(), this);

        pluginManager.registerEvents(new PluginCommandListener(), this);
        pluginManager.registerEvents(new NickBookGUICommand(), this);

        getCommand("nick").setExecutor(new NickCommand());
        getCommand("unnick").setExecutor(new UnNickCommand());
        getCommand("nickbookgui").setExecutor(new NickBookGUICommand());

        DisguiseManager.initialize(this, false);
        Bukkit.getConsoleSender().sendMessage(aboutMessage);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        remoteDatabase.close();
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    public void connectDatabase() {
        MySQL mySQL = new MySQL();
        long time = System.currentTimeMillis();
        if (!mySQL.connect()) {
            this.getLogger().severe(Messages.translateCC("&c无法连接至MySQL服务器!"));

            Bukkit.getPluginManager().disablePlugin(this);
        }
        if (System.currentTimeMillis() - time >= 5000) {
            this.getLogger().warning(Messages.translateCC("&e警告! 数据库连接时间过长. 可能会导致效果不佳. " + ((System.currentTimeMillis() - time) / 1000) + "ms"));
        }

        remoteDatabase = mySQL;
        remoteDatabase.init();
    }

}
