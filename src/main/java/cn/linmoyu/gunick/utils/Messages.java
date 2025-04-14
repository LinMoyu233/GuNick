package cn.linmoyu.gunick.utils;

public class Messages {

    public static String NICK_COMMAND_NO_PERMISSION_MESSAGE = Messages.translate("&c你没有使用该命令的权限!");
    public static String NICK_COMMAND_PLAYER_ONLY_MESSAGE = Messages.translate("&c只有玩家才能执行此命令.");
    public static String NICK_COMMAND_USAGE_PREFIX_MESSAGE = Messages.translate("&c用法: ");
    public static String NICK_COMMAND_USE_USAGE_MAIN_MESSAGE = Messages.translate(NICK_COMMAND_USAGE_PREFIX_MESSAGE + "/nick [昵称]");

    public static String NICK_ACTIONBAR_IN_NICK = Messages.translate("&f你目前&c已设置昵称");
    public static String NICK_ACTIONBAR_IN_NICK_APPEND_INVIS = Messages.translate(", 隐身");

    public static String NICK_SUCESSFUL_MESSAGE = Messages.translate("&a你已经完成昵称设置!");

    public static String UNNICK_SUCESSFUL_MESSAGE = Messages.translate("&a你的昵称已经重置!");
    public static String UNNICK_ALREADY_MESSAGE = Messages.translate("&c你没有设置昵称.");

    public static String NICK_TOO_LONG = Messages.translate("&c你不能设置多于&e16&c个字符的昵称.");
    public static String NICK_TOO_SHORT = Messages.translate("&c你不能设置少于&e3&c个字符的昵称.");
    public static String NICK_CONTAINS_SPECIAL_CHAR_MESSAGE = Messages.translate("&c你只能设置带有这些符号的昵称: " + Config.nickAllowedChar);
    public static String NICK_PLAYER_NAME_KNOWN_MESSAGE = Messages.translate("&c你设置的昵称与某玩家相同, 请选择其他昵称!");


    public static String NICK_NOT_NICKED = Messages.translate("&c你没有设置昵称.");
    // Todo: 未来可能的集成隐身？
//    public static String NICK_ACTIONBAR_IN_INVIS = "&f你目前&c已设置隐身";


    public static String translate(String s) {
        return s.replace("&", "§");
    }
}
