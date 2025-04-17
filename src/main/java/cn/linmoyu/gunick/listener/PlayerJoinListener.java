package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.event.PlayerNickEvent;
import cn.linmoyu.gunick.utils.API;
import cn.linmoyu.gunick.utils.Config;
import cn.linmoyu.gunick.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    // callEvent, Nick逻辑交给事件监听处理
    public static void callNickEvent(Player player) {
        if (Config.isJoinNickAsyncAndReplaceMessage) {
            Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
                String nickName = API.getPlayerNick(player.getUniqueId());
                String nickedPrefix = API.getPlayerNickedPrefix(player.getUniqueId());
                String nickedSuffix = API.getPlayerNickSuffix(player.getUniqueId());
                Bukkit.getScheduler().runTask(GuNick.getPlugin(), () -> {
                    PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, player.getName(), nickName, true, nickedPrefix, nickedSuffix);
                    Bukkit.getPluginManager().callEvent(playerNickEvent);
                });
            });
        } else {
            String nickName = API.getPlayerNick(player.getUniqueId());
            String nickedPrefix = API.getPlayerNickedPrefix(player.getUniqueId());
            String nickedSuffix = API.getPlayerNickSuffix(player.getUniqueId());
            PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, player.getName(), nickName, true, nickedPrefix, nickedSuffix);
            Bukkit.getPluginManager().callEvent(playerNickEvent);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 防止可能的后遗症
        player.setDisplayName(player.getName());
        Messages.handleLobbyActionBar(player);

        // 如果玩家没有匿名, 不执行逻辑
        if (Config.isJoinNickAsyncAndReplaceMessage) {

            Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
                if (!API.isPlayerNickedDataBase(player.getUniqueId())) return;
                callNickEvent(player);
            });

        } else {
            if (!API.isPlayerNickedDataBase(player.getUniqueId())) return;
            callNickEvent(player);
        }

    }


}
