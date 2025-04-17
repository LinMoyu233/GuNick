package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.database.PlayerData;
import cn.linmoyu.gunick.event.PlayerNickEvent;
import cn.linmoyu.gunick.utils.API;
import cn.linmoyu.gunick.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 防止可能的后遗症
        player.setDisplayName(player.getName());
        // 常驻任务
        Messages.handleLobbyActionBar(player);

        // 加载数据
        UUID playerUUID = player.getUniqueId();
        PlayerData data = GuNick.getRemoteDatabase().loadPlayerData(playerUUID);
        if (data != null) {
            GuNick.getPlugin().getDataCache().put(playerUUID, data);
        } else return;
        String playerName = API.getPlayerName(player);
        String nickName = data.getNickname();
        String nickedPrefix = data.getNickedPrefix();
        String nickedSuffix = data.getNickedSuffix();
        PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, playerName, nickName, true, nickedPrefix, nickedSuffix);
        Bukkit.getPluginManager().callEvent(playerNickEvent);

    }
}
