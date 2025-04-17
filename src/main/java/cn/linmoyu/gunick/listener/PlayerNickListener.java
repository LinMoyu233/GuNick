package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.event.PlayerNickEvent;
import cn.linmoyu.gunick.utils.Messages;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerNickListener implements Listener {

    @EventHandler
    public void onNick(PlayerNickEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String nickName = event.getNickName();
        Disguise disguise = Disguise.builder()
                .setName(nickName)
                .build();
        DisguiseResponse response = GuNick.getPlugin().getDisguiseProvider().disguise(player, disguise);
        String version = GuNick.getPlugin().getDescription().getVersion();
        switch (response) {
            case SUCCESS:
                if (event.needRefresh()) GuNick.getPlugin().getDisguiseProvider().refreshAsPlayer(player);
                player.setDisplayName(nickName);
                break;
            case FAIL_NAME_INVALID:
                event.setCancelled(true);
                player.sendMessage(Messages.NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE);
                break;
            case FAIL_NAME_TOO_LONG:
                event.setCancelled(true);
                player.sendMessage(Messages.translateCC(Messages.NICK_FAIL_TOO_LONG_MESSAGE + " (v" + version + ")"));
                break;
            case FAIL_NAME_ALREADY_ONLINE:
                event.setCancelled(true);
                player.sendMessage(Messages.translateCC(Messages.NICK_FAIL_PLAYER_NAME_KNOWN_MESSAGE + " (v" + version + ")"));
                break;
            default:
                event.setCancelled(true);
                player.sendMessage(Messages.translateCC("&c在当前子服对你匿名时出错. 上报管理员时请提供以下错误, 并附带所在模式或大厅: " + response + ". (v" + version + ")"));
                break;
        }
    }
}
