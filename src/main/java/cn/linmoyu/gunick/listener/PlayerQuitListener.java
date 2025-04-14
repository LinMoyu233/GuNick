package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.event.PlayerUnNickEvent;
import cn.linmoyu.gunick.utils.API;
import cn.linmoyu.gunick.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!API.isPlayerNicked(player.getUniqueId())) return;

        PlayerUnNickEvent playerUnNickEvent = new PlayerUnNickEvent(player, false);
        Bukkit.getPluginManager().callEvent(playerUnNickEvent);

        // 停止大厅的ActionBar消息任务
        if (Config.isLobby) {
            BukkitTask task = GuNick.getPlugin().getTasks().remove(player);
            if (task != null) task.cancel();
        }
    }
}
