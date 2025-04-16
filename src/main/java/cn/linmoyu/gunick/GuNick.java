package cn.linmoyu.gunick;

import cn.linmoyu.gunick.command.NickBookGuiCommand;
import cn.linmoyu.gunick.command.NickCommand;
import cn.linmoyu.gunick.command.UnNickCommand;
import cn.linmoyu.gunick.database.Database;
import cn.linmoyu.gunick.database.MySQL;
import cn.linmoyu.gunick.listener.*;
import cn.linmoyu.gunick.nms.VersionSupport;
import cn.linmoyu.gunick.nms.v1_8;
import cn.linmoyu.gunick.utils.Config;
import cn.linmoyu.gunick.utils.Messages;
import com.comphenix.protocol.ProtocolLibrary;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@Getter
public final class GuNick extends JavaPlugin {
    @Getter
    private static GuNick plugin;
    @Getter
    private static VersionSupport versionSupport;
    @Getter
    private static Database remoteDatabase;
    @Getter
    private static HashMap<String, String> nickPlayersName = new HashMap<>();
    @Getter
    private DisguiseProvider disguiseProvider = DisguiseManager.getProvider();

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
        if (pluginManager.isPluginEnabled("ProtocolLib")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new ChatPacketListener(this));
        }

        getCommand("nick").setExecutor(new NickCommand());
        getCommand("unnick").setExecutor(new UnNickCommand());
        getCommand("nickbookgui").setExecutor(new NickBookGuiCommand());

        DisguiseManager.initialize(this, false);
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
            this.getLogger().warning(Messages.translateCC("&e警告! 数据库连接时间过长. 可能会出现延迟匿名, 从而导致导致效果不佳. " + ((System.currentTimeMillis() - time) / 1000) + "ms"));
        }

        remoteDatabase = mySQL;
        remoteDatabase.init();
    }

}
