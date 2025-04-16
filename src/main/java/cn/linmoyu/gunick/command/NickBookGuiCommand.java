package cn.linmoyu.gunick.command;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.utils.*;
import com.meteor.bookapi.Book;
import com.meteor.bookapi.BookApi;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class NickBookGuiCommand implements CommandExecutor {

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
        // 第1步: 阅读提示 填充arg[0]
        if (args.length == 0) {
            Book book = new Book();
            TextComponent text = new TextComponent(Messages.translateCC("&0设置匿名将允许你使用不同的用户名进行游戏, 以避免被他人认出.\n\n所有规则仍然适用. 你仍然可以被人工举报并且所有的匿名将会被记录."));
            TextComponent iknow = new TextComponent(Messages.translateCC("\n\n&0➤ &n我已知晓, &n开始设置匿名."));
            iknow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow"));
            iknow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点此开始").create()));
            text.addExtra(iknow);
            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

        // 第2步: 选Rank 填充arg[1]
        if (args.length == 1 && args[0].equals("iknow")) {
            Book book = new Book();
            TextComponent text = new TextComponent(Messages.translateCC("&0帮助你设置昵称!\n首先, 你需要选择你想要显示的&l会员等级.\n\n"));

            TextComponent normalRank = new TextComponent(Messages.translateCC("&0➤ &7默认\n"));
            normalRank.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow normal"));
            normalRank.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 显示为&7默认")).create()));
            text.addExtra(normalRank);

            TextComponent vipRank = new TextComponent(Messages.translateCC("&0➤ &aVIP\n"));
            vipRank.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow vip"));
            vipRank.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 显示为&aVIP")).create()));
            text.addExtra(vipRank);

            TextComponent mvpPlus = new TextComponent(Messages.translateCC("&0➤ &bMVP&c+\n"));
            mvpPlus.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow mvpplus"));
            mvpPlus.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 显示为&bMVP&c+")).create()));
            text.addExtra(mvpPlus);

            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

        // 第3步: 设置自定义名字方式 填充arg[2]
        if (args.length == 2 && args[0].equals("iknow")) {
            Book book = new Book();
            TextComponent text = new TextComponent(Messages.translateCC("&0现在, 请选择你要使用的&0&l昵称!\n\n"));

            TextComponent inputNick = new TextComponent(Messages.translateCC("&0➤ &0输入昵称\n"));
            inputNick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow " + args[1] + " input"));
            inputNick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 输入你要使用的昵称")).create()));
            text.addExtra(inputNick);

            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

        // 第4步: 自定义名字 SignGUI 填充arg[3]
        if (args.length == 3 && args[0].equals("iknow") && args[2].equals("input")) {
            try {
                SignGUI gui = SignGUI.builder()
                        .setLines(null, "^^^^^^^^^^^^^^^", "在此输入你", "喜欢的昵称")
                        .setType(Material.SIGN_POST)
                        .setColor(DyeColor.YELLOW)
                        .setHandler((p, result) -> {
                            String line0 = result.getLine(0);
                            Bukkit.getScheduler().runTask(GuNick.getPlugin(), () -> {
                                String commandArgs = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
                                player.chat("/" + label + " " + commandArgs + " " + line0); // 想在Console看见玩家发指令而已
                            });
                            return Collections.emptyList();
                        })
                        .build();
                gui.open(player);
            } catch (SignGUIVersionException e) {
                GuNick.getPlugin().getLogger().severe(Messages.translateCC("&cSignGUI不支持当前Minecraft版本."));
            }
            return true;
        }

        // 第5步: 检查名字 / 设置前后缀
        if (args.length == 4 && args[0].equals("iknow")) {
            String nickName = args[3];
            // 需要返回理由, 所以改一下形式, 返回空为可以使用.
            if (!validNickName(player, nickName).isEmpty()) {
                String commandArgs = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 2)); // 回到选择输入昵称
                player.chat("/" + label + " " + commandArgs + " nameInvaild " + validNickName(player, nickName)); // 错误方式+理由
                return true;
            }

            // 如果匿名没问题
            Book book = new Book();
            TextComponent text = new TextComponent(Messages.translateCC("&0哇哦！显示昵称时, 你想要更改前缀么?\n\n"));

            TextComponent inputNick = new TextComponent(Messages.translateCC("&0➤ &0输入昵称\n"));
            inputNick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow " + args[1] + " input"));
            inputNick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 输入你要使用的昵称")).create()));
            text.addExtra(inputNick);

            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

        // 第5.5步 匿名有问题
        if (args.length >= 4 && args[0].equals("iknow") && args[2].equals("nameInvaild")) {
            Book book = new Book();
            String deniedReason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            TextComponent text = new TextComponent(Messages.translateCC("&c&l你的昵称有些问题, &c&l请更换后重试:\n" + deniedReason + "\n\n现在, 请选择你要使用的&0&l昵称!\n\n"));

            TextComponent inputNick = new TextComponent(Messages.translateCC("&0➤ &0输入昵称\n"));
            inputNick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow " + args[1] + " input"));
            inputNick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.translateCC("点击这里, 输入你要使用的昵称")).create()));
            text.addExtra(inputNick);

            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

//

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

    private String validNickName(Player player, String nickName) {
        if (!Config.namePattern.matcher(nickName).matches()) {
            return (Messages.NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE);
        }
        if (nickName.length() < Config.nickMinLength) {
            return (Messages.NICK_FAIL_TOO_SHORT);
        }
        if (nickName.length() > Config.nickLength) {
            return (Messages.NICK_FAIL_TOO_LONG);
        }
        // 如果nickName包含玩家名字
        if (nickName.equals(player.getName())) {
            if (!API.isPlayerNicked(player)) {
                // 如果玩家没有匿名
                return (Messages.NICK_FAIL_AS_SELF_MESSAGE);
            }
        }
        // 如果玩家匿名了, 使用lib自带的查询要nickname的名字是否是真实名字
        if (API.getPlayerNameOnline(player).equals(nickName)) {
            return (Messages.NICK_FAIL_AS_SELF_MESSAGE);
        }
        return "";
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