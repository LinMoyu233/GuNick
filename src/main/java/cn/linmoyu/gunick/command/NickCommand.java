package cn.linmoyu.gunick.command;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.event.PlayerNickEvent;
import cn.linmoyu.gunick.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NickCommand implements CommandExecutor {

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
        // 给予用法
        if (strings.length < 1) {
            player.sendMessage(Messages.NICK_COMMAND_USE_USAGE_MAIN_MESSAGE);
            return true;
        }
        // 获取玩家需要nick的名字
        String nickName = strings[0];
        // 如果玩家名为reload并且有重载权限, 重载插件
        if (nickName.equalsIgnoreCase("reload") && player.hasPermission(Permissions.NICK_ADMIN_RELOAD_PERMISSION)) {
            Config.reloadConfig(GuNick.getPlugin());
            return true;
        }

        // 最小 最大 长度
        if (nickName.length() < Config.nickMinLength) {
            player.sendMessage(Messages.NICK_TOO_SHORT);
            return true;
        } else if (nickName.length() > Config.nickLength) {
            player.sendMessage(Messages.NICK_TOO_LONG);
            return true;
        }
        String playerName = player.getName(); // 需要提前缓存playerName, 否则后续存储的还是nick名字
//        // 判断字符
//        if (playerName.matches(Config.nickAllowedChar)) {
//            player.sendMessage(Messages.NICK_CONTAINS_SPECIAL_CHAR_MESSAGE);
//            return true;
//        }
        String prefix = LuckPermsUtil.getPrefix(player.getUniqueId());
        String suffix = LuckPermsUtil.getSuffix(player.getUniqueId());
        String nickedPrefix;
        String nickedSuffix;
        if (strings.length >= 2) {
            nickedPrefix = Messages.translate(strings[1]);
        } else {
            nickedPrefix = "";
        }
        if (strings.length >= 3) {
            nickedSuffix = Messages.translate(strings[2]);
        } else {
            nickedSuffix = "";
        }

        // callEvent, Nick逻辑交给事件监听处理
        PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, nickName, true, nickedPrefix, nickedSuffix);
        Bukkit.getPluginManager().callEvent(playerNickEvent);
        // 如果事件没有被取消, 存储NickName去数据库
        if (!playerNickEvent.isCancelled()) {
            Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
                UUID uuid = player.getUniqueId();
                API.setPlayerNickToDatabase(uuid, playerName, nickName, prefix, suffix, nickedPrefix, nickedSuffix);
                if (!nickedPrefix.isEmpty()) LuckPermsUtil.setPrefix(uuid, nickedPrefix);
                if (!nickedSuffix.isEmpty()) LuckPermsUtil.setSuffix(uuid, nickedSuffix);
            });
            player.sendMessage(Messages.NICK_SUCESSFUL_MESSAGE);
        }
        return true;
    }
}
