package cn.linmoyu.gunick.utils;

import cn.linmoyu.gunick.GuNick;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class Messages {

    private static final HashMap<Player, BukkitTask> actionBarTasks = new HashMap<>();

    public static String NICK_COMMAND_NO_PERMISSION_MESSAGE;
    public static String NICK_COMMAND_PLAYER_ONLY_MESSAGE;
    public static String NICK_COMMAND_USAGE_PREFIX_MESSAGE;
    public static String NICK_COMMAND_USAGE_MAIN_MESSAGE;

    public static String NICK_COMMAND_ONLY_LOBBY_MESSAGE;

    public static String NICK_ACTIONBAR_IN_NICK;
    public static String NICK_ACTIONBAR_IN_NICK_APPEND_ONLY_GAME;
    public static String NICK_ACTIONBAR_IN_NICK_APPEND_INVIS;
    public static String NICK_ACTIONBAR_IN_INVIS;

    public static String NICK_SUCCESSFUL_MESSAGE;
    public static String NICK_RELOAD_MESSAGE;

    public static String UNNICK_SUCCESSFUL_MESSAGE;

    public static String NICK_FAIL_AS_SELF_MESSAGE;
    public static String NICK_FAIL_TOO_LONG_MESSAGE;
    public static String NICK_FAIL_TOO_SHORT_MESSAGE;
    public static String NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE;
    public static String NICK_FAIL_PLAYER_NAME_KNOWN_MESSAGE;

    public static String NICK_GUI_WARNING;
    public static String NICK_GUI_WARNING_IKNOW;
    public static String NICK_GUI_WARNING_IKNOW_HOVER;

    public static String NICK_GUI_RANK_TEXT;
    public static String NICK_GUI_RANKS;
    public static String NICK_GUI_RANKS_HOVER;

    public static String NICK_GUI_NICKNAME;
    public static String NICK_GUI_NICKNAME_INPUTNAME;
    public static String NICK_GUI_NICKNAME_INPUTNAME_HOVER;
    public static String NICK_GUI_NICKNAME_ERROR_IN_NICKNAME;

    public static String NICK_FINISH;

    public static String UNNICK_FAIL_ALREADY_MESSAGE;

    public static void loadMessages(FileConfiguration config) {
        NICK_COMMAND_NO_PERMISSION_MESSAGE = Messages.translateCC(config.getString("messages.no_permission"));
        NICK_COMMAND_PLAYER_ONLY_MESSAGE = Messages.translateCC(config.getString("messages.player_only"));
        NICK_COMMAND_USAGE_PREFIX_MESSAGE = Messages.translateCC(config.getString("messages.usage_prefix"));
        NICK_COMMAND_USAGE_MAIN_MESSAGE = Messages.translateCC(NICK_COMMAND_USAGE_PREFIX_MESSAGE + config.getString("messages.usage_main"));

        NICK_COMMAND_ONLY_LOBBY_MESSAGE = Messages.translateCC(config.getString("forceNickCommandOnGame.deniedMessage"));

        NICK_ACTIONBAR_IN_NICK = Messages.translateCC(config.getString("messages.actionbar_in_nick"));
        NICK_ACTIONBAR_IN_NICK_APPEND_ONLY_GAME = Messages.translateCC(config.getString("messages.actionbar_in_nick_append_only_game"));
        NICK_ACTIONBAR_IN_NICK_APPEND_INVIS = Messages.translateCC(config.getString("messages.actionbar_in_nick_append_invis"));
        NICK_ACTIONBAR_IN_INVIS = Messages.translateCC(config.getString("messages.actionbar_in_invis"));

        NICK_SUCCESSFUL_MESSAGE = Messages.translateCC(config.getString("messages.nick_successful"));
        NICK_RELOAD_MESSAGE = Messages.translateCC("&a已重载.");

        UNNICK_SUCCESSFUL_MESSAGE = Messages.translateCC(config.getString("messages.unnick_successful"));

        NICK_FAIL_AS_SELF_MESSAGE = Messages.translateCC(config.getString("messages.nick_fail_as_self"));
        NICK_FAIL_TOO_SHORT_MESSAGE = Messages.translateCC(config.getString("messages.nick_fail_too_short"));
        NICK_FAIL_TOO_LONG_MESSAGE = Messages.translateCC(config.getString("messages.nick_fail_too_long"));
        NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE = Messages.translateCC(config.getString("messages.nick_fail_contains_special_char") + Config.namePattern);
        NICK_FAIL_PLAYER_NAME_KNOWN_MESSAGE = Messages.translateCC(config.getString("messages.nick_fail_player_name_known_message"));

        UNNICK_FAIL_ALREADY_MESSAGE = Messages.translateCC(config.getString("messages.unnick_fail_already"));

        NICK_GUI_WARNING = Messages.translateCC(config.getString("messages.book.warning"));
        NICK_GUI_WARNING_IKNOW = Messages.translateCC(config.getString("messages.book.warning_iknow"));
        NICK_GUI_WARNING_IKNOW_HOVER = Messages.translateCC(config.getString("messages.book.warning_iknow_hover"));

        NICK_GUI_RANK_TEXT = Messages.translateCC(config.getString("messages.book.ranktext"));
        NICK_GUI_RANKS = Messages.translateCC(config.getString("messages.book.ranks"));
        NICK_GUI_RANKS_HOVER = Messages.translateCC(config.getString("messages.book.ranks_hover"));

        NICK_GUI_NICKNAME = Messages.translateCC(config.getString("messages.book.nickname"));
        NICK_GUI_NICKNAME_INPUTNAME = Messages.translateCC(config.getString("messages.book.nickname_inputname"));
        NICK_GUI_NICKNAME_INPUTNAME_HOVER = Messages.translateCC(config.getString("messages.book.nickname_inputname_hover"));
        NICK_GUI_NICKNAME_ERROR_IN_NICKNAME = Messages.translateCC(config.getString("messages.book.nickname_error_in_nickname"));

        NICK_FINISH = Messages.translateCC(config.getString("messages.book.nick_finish"));
    }

    public static String translateCC(String s) {
        return s.replaceAll("&", "§").replaceAll("%nl%", "\n");
    }

    public static void handleLobbyActionBar(Player player) {
        if (!Config.actionbar_enable) return;
        // 取消现有任务防止重复
        BukkitTask runningTask = actionBarTasks.getOrDefault(player, null);
        if (runningTask != null) {
            runningTask.cancel();
        }

        // 大神 SuperVanish / PremiumVanish 被迫在task里套拼接
        // 还有好多处理 摆烂了 就让他常驻吧
        actionBarTasks.put(player, (
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        StringBuilder actionBarBuilder = new StringBuilder();
                        if (Config.actionbar_vanishbar && API.isPlayerVanished(player) && !API.isPlayerDataNicked(player))
                            actionBarBuilder.append(Messages.NICK_ACTIONBAR_IN_INVIS);

                        if (API.isPlayerDataNicked(player)) {
                            actionBarBuilder.append(NICK_ACTIONBAR_IN_NICK);
                            if (!player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
                                actionBarBuilder.append(NICK_ACTIONBAR_IN_NICK_APPEND_ONLY_GAME);
                            }
                            if (API.isPlayerVanished(player)) {
                                actionBarBuilder.append(NICK_ACTIONBAR_IN_NICK_APPEND_INVIS);
                            }
                        }
                        GuNick.getVersionSupport().playAction(player, actionBarBuilder.toString());
                    }
                }.runTaskTimerAsynchronously(GuNick.getPlugin(), 0L, 20L)));
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
