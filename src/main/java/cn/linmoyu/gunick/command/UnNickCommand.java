package cn.linmoyu.gunick.command;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.event.PlayerUnNickEvent;
import cn.linmoyu.gunick.utils.API;
import cn.linmoyu.gunick.utils.LuckPermsUtil;
import cn.linmoyu.gunick.utils.Messages;
import cn.linmoyu.gunick.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnNickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // 判断是否为后台 是则终止
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Messages.NICK_COMMAND_PLAYER_ONLY_MESSAGE);
            return true;
        }

        // 获取玩家
        Player player = (Player) commandSender;
        // 判断权限
        if (!player.hasPermission(Permissions.NICK_USE_PERMISSION)) {
            player.sendMessage(Messages.NICK_COMMAND_NO_PERMISSION_MESSAGE);
            return true;
        }

        // callEvent, UnNick逻辑交给事件监听处理
        PlayerUnNickEvent playerUnNickEvent = new PlayerUnNickEvent(player, true);
        Bukkit.getPluginManager().callEvent(playerUnNickEvent);
        // 如果事件没有被取消, 则移除数据库里的Nick?
        if (!playerUnNickEvent.isCancelled()) {
            Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
                UUID uuid = player.getUniqueId();
                API.deletePlayerNickFromDatabase(uuid);
                String nickedPrefix = API.getPlayerNickedPrefix(uuid);
                String nickedSuffix = API.getPlayerNickSuffix(uuid);
                if (!nickedPrefix.isEmpty()) LuckPermsUtil.setPrefix(uuid, nickedPrefix);
                if (!nickedSuffix.isEmpty()) LuckPermsUtil.setSuffix(uuid, nickedSuffix);
            });
        }

        return true;
    }
}
