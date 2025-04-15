package cn.linmoyu.gunick.utils;

import cn.linmoyu.gunick.GuNick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class Messages {

    private static final HashMap<Player, BukkitTask> actionBarTasks = new HashMap<>();

    public static String NICK_COMMAND_NO_PERMISSION_MESSAGE = Messages.translate("&c你没有使用该命令的权限!");
    public static String NICK_COMMAND_PLAYER_ONLY_MESSAGE = Messages.translate("&c只有玩家才能执行此命令.");
    public static String NICK_COMMAND_USAGE_PREFIX_MESSAGE = Messages.translate("&c用法: ");
    public static String NICK_COMMAND_USE_USAGE_MAIN_MESSAGE = Messages.translate(NICK_COMMAND_USAGE_PREFIX_MESSAGE + "/nick [昵称]");

    public static String NICK_ACTIONBAR_IN_NICK = Messages.translate("&f你目前&c已设置昵称");
    public static String NICK_ACTIONBAR_IN_NICK_NO_LOBBY = Messages.translate("(仅游戏)");
    public static String NICK_ACTIONBAR_IN_NICK_APPEND_INVIS = Messages.translate("&f, &c已隐身");

    public static String NICK_SUCESSFUL_MESSAGE = Messages.translate("&a你已经完成昵称设置!");
    public static String NICK_RELOAD_MESSAGE = Messages.translate("&a已重载.");

    public static String UNNICK_SUCESSFUL_MESSAGE = Messages.translate("&a你的昵称已经重置!");

    public static String NICK_FAIL_AS_SELF_MESSAGE = Messages.translate("&c你不能设置与自己一样的昵称!");
    public static String NICK_FAIL_TOO_LONG = Messages.translate("&c你不能设置多于&e16&c个字符的昵称.");
    public static String NICK_FAIL_TOO_SHORT = Messages.translate("&c你不能设置少于&e3&c个字符的昵称.");
    public static String NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE = Messages.translate("&c你只能设置带有这些符号的昵称: " + Config.namePattern);
    public static String NICK_FAIL_PLAYER_NAME_KNOWN_MESSAGE = Messages.translate("&c你设置的昵称与某玩家相同, 请选择其他昵称!");

    public static String UNNICK_FAIL_ALREADY_MESSAGE = Messages.translate("&c你没有设置昵称.");
    // Todo: 未来可能的集成隐身？
//    public static String NICK_ACTIONBAR_IN_INVIS = "&f你目前&c已设置隐身";


    public static String translate(String s) {
        return s.replace("&", "§");
    }

    public static void handleLobbyActionBar(Player player) {
        // 取消现有任务防止重复
        BukkitTask runningTask = actionBarTasks.getOrDefault(player, null);
        if (runningTask != null) {
            runningTask.cancel();
        }

        StringBuilder actionBarBuilder = new StringBuilder(Messages.NICK_ACTIONBAR_IN_NICK);
        if (!player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
            actionBarBuilder.append(NICK_ACTIONBAR_IN_NICK_NO_LOBBY);
        }
        if (API.isPlayerVanished(player)) {
            actionBarBuilder.append(NICK_ACTIONBAR_IN_NICK_APPEND_INVIS);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                GuNick.getPlugin(),
                () -> GuNick.getVersionSupport().playAction(player, actionBarBuilder.toString()),
                0L, 20L
        );
        actionBarTasks.put(player, task);
    }

    public static void handleCancelLobbyActionBar(Player player) {
        if (Config.isLobby) {
            BukkitTask runningTask = actionBarTasks.get(player);
            if (runningTask != null) {
                runningTask.cancel();
                actionBarTasks.remove(player);
            }
        }
    }
}
