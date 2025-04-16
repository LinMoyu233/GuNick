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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 判断是否为后台 是则终止
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.NICK_COMMAND_PLAYER_ONLY_MESSAGE);
            return true;
        }

        Player player = (Player) sender;

        // 权限校验
        if (!player.hasPermission(Permissions.NICK_USE_PERMISSION)) {
            player.sendMessage(Messages.NICK_COMMAND_NO_PERMISSION_MESSAGE);
            return true;
        }

        // 如果Nick的玩家名为reload, 且有重载权限
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission(Permissions.NICK_ADMIN_RELOAD_PERMISSION)) return true;
            Config.reloadConfig(GuNick.getPlugin());
            player.sendMessage(Messages.NICK_RELOAD_MESSAGE);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
            player.performCommand("unnick");
            return true;
        }
        if (Config.bookGui) {
            player.performCommand("nickbookgui");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Messages.NICK_COMMAND_USE_USAGE_MAIN_MESSAGE);
            return true;
        }

        String nickName = args[0];
        if (!validNickName(player, nickName)) {
            return true;
        }
        String playerName = player.getName(); // 需要提前缓存playerName, 否则后续存储的还是nick名字

        // 处理需要匿名的前缀后缀
        String[] prefixSuffix = processPrefixAndSuffix(args);
        String nickedPrefix = prefixSuffix[0];
        String nickedSuffix = prefixSuffix[1];

        // 大厅处理
        if (Config.isLobby) {
            Messages.handleLobbyActionBar(player);
            // 如果玩家不能在大厅匿名 直接保存数据返回
            if (!player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
                saveNickData(player, playerName, nickName, nickedPrefix, nickedSuffix);
                return true;
            }
        }

        // 触发事件
        PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, playerName, nickName, true, nickedPrefix, nickedSuffix);
        Bukkit.getPluginManager().callEvent(playerNickEvent);
        if (playerNickEvent.isCancelled()) {
            return true;
        }

        // 保存数据
        saveNickData(player, playerName, nickName, nickedPrefix, nickedSuffix);

        player.sendMessage(Messages.NICK_SUCESSFUL_MESSAGE);
        return true;
    }

    private boolean validNickName(Player player, String nickName) {
        if (!Config.namePattern.matcher(nickName).matches()) {
            player.sendMessage(Messages.NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE);
            return false;
        }
        if (nickName.length() < Config.nickMinLength) {
            player.sendMessage(Messages.NICK_FAIL_TOO_SHORT);
            return false;
        }
        if (nickName.length() > Config.nickLength) {
            player.sendMessage(Messages.NICK_FAIL_TOO_LONG);
            return false;
        }
        // 如果nickName包含玩家名字
        if (nickName.equals(player.getName())) {
            // 如果玩家没有匿名
            if (!API.isPlayerNicked(player)) {
                player.sendMessage(Messages.NICK_FAIL_AS_SELF_MESSAGE);
                return false;
            }
        }
        if (API.getPlayerNameOnline(player).equals(nickName)) {
            // 如果玩家匿名了, 使用lib自带的查询要nickname的名字是否是真实名字
            player.sendMessage(Messages.NICK_FAIL_AS_SELF_MESSAGE);
            return false;
        }
        return true;
    }

    private String[] processPrefixAndSuffix(String[] args) {
        String prefix = args.length >= 2 ? Messages.translateCC(args[1]) : "";
        String suffix = args.length >= 3 ? Messages.translateCC(args[2]) : "";
        return new String[]{prefix, suffix};
    }

    private void saveNickData(Player player, String playerName, String nickName, String nickedPrefix, String nickedSuffix) {
        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
            UUID uuid = player.getUniqueId();
            String prefix = LuckPermsUtil.getPrefix(uuid);
            String suffix = LuckPermsUtil.getSuffix(uuid);
            API.setPlayerNickToDatabase(uuid, playerName, nickName,
                    prefix, suffix, nickedPrefix, nickedSuffix);
            if (!nickedPrefix.isEmpty()) LuckPermsUtil.setPrefix(uuid, nickedPrefix);
            if (!nickedSuffix.isEmpty()) LuckPermsUtil.setSuffix(uuid, nickedSuffix);
        });
    }

}