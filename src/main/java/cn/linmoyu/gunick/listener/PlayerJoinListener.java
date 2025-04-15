package cn.linmoyu.gunick.listener;

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

public class PlayerJoinListener implements Listener {

    // callEvent, Nick逻辑交给事件监听处理
    public static void callNickEvent(Player player) {
//        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
        String nickName = API.getPlayerNick(player.getUniqueId());
        String nickedPrefix = API.getPlayerNickedPrefix(player.getUniqueId());
        String nickedSuffix = API.getPlayerNickSuffix(player.getUniqueId());
//            Bukkit.getScheduler().runTask(GuNick.getPlugin(), () -> {
        PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, player.getName(), nickName, false, nickedPrefix, nickedSuffix);
        Bukkit.getPluginManager().callEvent(playerNickEvent);
//            });
//        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
//        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        Player player = event.getPlayer();

        // 防止可能的后遗症
        player.setDisplayName(player.getName());

        // 如果玩家没有匿名, 不执行逻辑
//        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
//            if (!player.isOnline()) return;
        if (!API.isPlayerNicked(player.getUniqueId())) return;

        // 大厅逻辑
        if (Config.isLobby) {
            handleLobby(player);
            return;
        }

        // 不在大厅 直接执行匿名逻辑
        callNickEvent(player);
//        });
    }

    public void handleLobby(Player player) {
        // 仅有权限的玩家可以在大厅匿名
        if (player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
            callNickEvent(player);
        }

        Messages.handleLobbyActionBar(player);
    }


}
