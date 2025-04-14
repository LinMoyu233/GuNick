package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.event.PlayerNickEvent;
import cn.linmoyu.gunick.utils.API;
import cn.linmoyu.gunick.utils.Config;
import cn.linmoyu.gunick.utils.Messages;
import cn.linmoyu.gunick.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

public class PlayerJoinListener implements Listener {

    // callEvent, Nick逻辑交给事件监听处理
    public static void callNickEvent(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
            String nickName = API.getPlayerNick(player.getUniqueId());
            String nickedPrefix = API.getPlayerNickedPrefix(player.getUniqueId());
            String nickedSuffix = API.getPlayerNickSuffix(player.getUniqueId());
            Bukkit.getScheduler().runTask(GuNick.getPlugin(), () -> {
                PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, nickName, false, nickedPrefix, nickedSuffix);
                Bukkit.getPluginManager().callEvent(playerNickEvent);
            });
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 防止可能的后遗症
        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());

        // 如果玩家没有匿名, 不执行逻辑
        if (!API.isPlayerNicked(player.getUniqueId())) return;

        // 大厅逻辑
        if (Config.isLobby) {
            handleLobby(player);
            return;
        }

        // 不在大厅 直接执行匿名逻辑
        callNickEvent(player);
    }

    public void handleLobby(Player player) {
        // 仅有权限的玩家可以在大厅匿名
        if (player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
            callNickEvent(player);
        }

        // ActionBar 提示
        StringBuilder lobbyActionbar = new StringBuilder(Messages.NICK_ACTIONBAR_IN_NICK);
        if (API.isPlayerVanished(player))
            lobbyActionbar.append(Messages.NICK_ACTIONBAR_IN_NICK_APPEND_INVIS); // 有INVIS则加ActionBar后缀

        // callEvent, Nick逻辑交给事件监听处理
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(GuNick.getPlugin(), () -> GuNick.getVersionSupport().playAction(player, lobbyActionbar.toString()), 0L, 20L);
        GuNick.getPlugin().getTasks().put(player, task);
    }


}
