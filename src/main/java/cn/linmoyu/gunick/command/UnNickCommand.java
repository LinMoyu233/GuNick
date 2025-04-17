package cn.linmoyu.gunick.command;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.database.PlayerData;
import cn.linmoyu.gunick.event.PlayerUnNickEvent;
import cn.linmoyu.gunick.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnNickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 判断是否为后台 是则终止
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.NICK_COMMAND_PLAYER_ONLY_MESSAGE);
            return true;
        }
        if (!Config.isLobby && !Config.forceNickCommandOnGame) {
            sender.sendMessage(Messages.NICK_COMMAND_ONLY_LOBBY_MESSAGE);
            return true;
        }

        // 获取玩家
        Player player = (Player) sender;
        // 判断权限
        if (!player.hasPermission(Permissions.NICK_USE_PERMISSION)) {
            player.sendMessage(Messages.NICK_COMMAND_NO_PERMISSION_MESSAGE);
            return true;
        }

        // 大厅处理
        if (Config.isLobby && !player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
            Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
                if (!API.isPlayerDataNicked(player)) {
                    player.sendMessage(Messages.UNNICK_FAIL_ALREADY_MESSAGE);
                    return;
                }
                removeNickData(player);
                player.sendMessage(Messages.UNNICK_SUCCESSFUL_MESSAGE);
            });
            return true;
        }

        PlayerUnNickEvent playerUnNickEvent = new PlayerUnNickEvent(player, true);
        Bukkit.getPluginManager().callEvent(playerUnNickEvent);
        // 如果事件没有被取消, 则移除数据库里的Nick?
        if (playerUnNickEvent.isCancelled()) return true;

        removeNickData(player);
        return true;
    }

    public void removeNickData(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = GuNick.getPlugin().getDataCache().get(playerUUID);
        if (playerData == null) return;
        String prefix = playerData.getPrefix();
        String suffix = playerData.getSuffix();
        if (!prefix.isEmpty()) LuckPermsUtil.setPrefix(playerUUID, prefix);
        if (!suffix.isEmpty()) LuckPermsUtil.setSuffix(playerUUID, suffix);
        API.deletePlayerNickFromDatabase(playerUUID);
    }
}
