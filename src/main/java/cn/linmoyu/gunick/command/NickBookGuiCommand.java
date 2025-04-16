package cn.linmoyu.gunick.command;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.utils.*;
import com.meteor.bookapi.Book;
import com.meteor.bookapi.BookApi;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class NickBookGuiCommand implements CommandExecutor {
    private static final HashMap<UUID, String> nickRank = new HashMap<>();

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

        // 开始叠史山了
        if (args.length == 0) {
            Book book = new Book();
            TextComponent text = new TextComponent(Messages.translateCC("&0设置匿名将允许你使用不同的用户名进行游戏, 以避免被他人认出.\n\n所有规则仍然适用. 你仍然可以被人工举报并且所有的匿名将会被记录."));
            TextComponent iknowwhatiamdoing = new TextComponent(Messages.translateCC("\n\n&0➤ &n我已知晓, &n开始设置匿名."));
            iknowwhatiamdoing.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknowwhatiamdoing"));
            iknowwhatiamdoing.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点此开始").create()));
            text.addExtra(iknowwhatiamdoing);
            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

        if (args.length == 1 && args[0].equals("iknowwhatiamdoing")) {
            Book book = new Book();
            TextComponent text = new TextComponent(Messages.translateCC("&0帮助你设置昵称!\n首先, 你需要选择你想要显示的&l会员等级.\n\n"));

            TextComponent normalRank = new TextComponent(Messages.translateCC("&0➤ &7默认\n"));
            normalRank.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknowwhatiamdoing normal"));
            normalRank.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 显示为&7默认")).create()));
            text.addExtra(normalRank);

            TextComponent vipRank = new TextComponent(Messages.translateCC("&0➤ &aVIP\n"));
            vipRank.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknowwhatiamdoing vip"));
            vipRank.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 显示为&aVIP")).create()));
            text.addExtra(vipRank);

            TextComponent mvpPlus = new TextComponent(Messages.translateCC("&0➤ &bMVP&c+\n"));
            mvpPlus.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknowwhatiamdoing mvpplus"));
            mvpPlus.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 显示为&bMVP&c+")).create()));
            text.addExtra(mvpPlus);

            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

//
//        String nickName = args[0];
//        if (!validNickName(player, nickName)) {
//            return true;
//        }
//        String playerName = player.getName(); // 需要提前缓存playerName, 否则后续存储的还是nick名字
//
//        // 处理需要匿名的前缀后缀
//        String[] prefixSuffix = processPrefixAndSuffix(args);
//        String nickedPrefix = prefixSuffix[0];
//        String nickedSuffix = prefixSuffix[1];
//
//        // 大厅处理
//        if (Config.isLobby) {
//            Messages.handleLobbyActionBar(player);
//            // 如果玩家不能在大厅匿名 直接保存数据返回
//            if (!player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
//                saveNickData(player, playerName, nickName, nickedPrefix, nickedSuffix);
//                return true;
//            }
//        }
//
//        // 触发事件
//        PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, playerName, nickName, true, nickedPrefix, nickedSuffix);
//        Bukkit.getPluginManager().callEvent(playerNickEvent);
//        if (playerNickEvent.isCancelled()) {
//            return true;
//        }
//
//        // 保存数据
//        saveNickData(player, playerName, nickName, nickedPrefix, nickedSuffix);
//
//        player.sendMessage(Messages.NICK_SUCESSFUL_MESSAGE);
//        return true;
        return true;
    }

    private boolean validNickName(Player player, String nickName) {
        if (nickName.length() < Config.nickMinLength) {
            player.sendMessage(Messages.NICK_FAIL_TOO_SHORT);
            return false;
        }
        if (nickName.length() > Config.nickLength) {
            player.sendMessage(Messages.NICK_FAIL_TOO_LONG);
            return false;
        }
        if (nickName.equals(player.getName())) {
            player.sendMessage(Messages.NICK_FAIL_AS_SELF_MESSAGE);
            return false;
        }
        if (!Config.namePattern.matcher(nickName).matches()) {
            player.sendMessage(Messages.NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE);
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