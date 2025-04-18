package cn.linmoyu.gunick.command;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.database.PlayerData;
import cn.linmoyu.gunick.event.PlayerNickEvent;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NickBookGUICommand implements CommandExecutor, Listener {

    private final ArrayList<String> blockString = new ArrayList<>(Arrays.asList(
            "rank", "name", "inputname"
    ));

    private final ConcurrentHashMap<UUID, PlayerData> nickGuiCache = new ConcurrentHashMap<>();

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

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // 权限校验
        if (!player.hasPermission(Permissions.NICK_USE_PERMISSION)) {
            player.sendMessage(Messages.NICK_COMMAND_NO_PERMISSION_MESSAGE);
            return true;
        }

        // handle设置参数指令
        if (args.length >= 1 && blockString.stream().anyMatch(args[0]::equalsIgnoreCase) && isPlayerOnBookGuiCache(playerUUID)) {
            handleProcess(player, args);
            return true;
        }

        // 开始叠史山了
        // 阅读提示 填充arg[0]
        // 玩家需不在 BookGUI匿名缓存中
        if (args.length == 0 && !isPlayerOnBookGuiCache(playerUUID)) {
            Book book = new Book();
            TextComponent warning = new TextComponent(Messages.NICK_GUI_WARNING);
            TextComponent iknow = new TextComponent(Messages.NICK_GUI_WARNING_IKNOW);
            iknow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui iknow"));
            iknow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.NICK_GUI_WARNING_IKNOW_HOVER).create()));
            warning.addExtra(iknow);

            book.addPage(warning);
            BookApi.openBook(player, book);
            return true;
        }

        // 阅读提示后 创建playerData 并缓存
        if (args.length == 1 && args[0].equalsIgnoreCase("iknow") && !isPlayerOnBookGuiCache(playerUUID)) {
            nickGuiCache.put(playerUUID, new PlayerData(
                    playerUUID,
                    API.getPlayerName(player),
                    "",
                    "",
                    "",
                    "",
                    ""
            ));
        }

        // BookGUI匿名缓存中里有没有playerData
        if (!isPlayerOnBookGuiCache(playerUUID)) {
            player.performCommand("nickbookgui");
            return true;
        }
        PlayerData playerNickGuiCache = nickGuiCache.get(playerUUID);

        // 匿名第1步: 设置前缀
        // 判断有没有nick前缀, 没有就设置
        if (playerNickGuiCache.getNickedPrefix().isEmpty()) {
            List<TextComponent> textComponentsPage = new ArrayList<>();
            FileConfiguration config = GuNick.getPlugin().getConfig();
            // 获取rank数量
            for (int i = 1; i <= 8; i++) {
                String permission = config.getString("ranks.rank" + i + ".permission");
                // 权限空指针, 没有这个项, 跳出循环
                if (permission == null) break;

                // 如果玩家有权限或者权限节点为空 增加到rank选择中
                if (permission.isEmpty() || player.hasPermission(permission)) {
                    String rankName = Messages.translateCC(config.getString("ranks.rank" + i + ".name")),
                            prefix = config.getString("ranks.rank" + i + ".prefix");

                    TextComponent textComponent = new TextComponent(
                            Messages.NICK_GUI_RANKS.replace("%rank%", rankName)
                    );

                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui rank " + prefix));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.NICK_GUI_RANKS_HOVER.replace("%rank%", rankName)).create()));

                    textComponentsPage.add(textComponent);
                }
            }
            Book book = new Book();
            TextComponent rankText = new TextComponent(Messages.NICK_GUI_RANK_TEXT);

            for (TextComponent component : textComponentsPage) {
                rankText.addExtra(component);
            }

            book.addPage(rankText);
            BookApi.openBook(player, book);
            return true;
        }

        // 没传入NickNameArg
        if (playerNickGuiCache.getNickname().isEmpty()) {
            Book book = new Book();
            TextComponent text = new TextComponent(Messages.NICK_GUI_NICKNAME);
            if (args.length >= 1 && args[0].equalsIgnoreCase("error")) {
                String error = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                TextComponent errorTC = new TextComponent(Messages.NICK_GUI_NICKNAME_ERROR_IN_NICKNAME + error + "\n\n");
                text.addExtra(errorTC);
            }

            TextComponent inputNick = new TextComponent(Messages.NICK_GUI_NICKNAME_INPUTNAME);
            inputNick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nickbookgui inputname"));
            inputNick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.NICK_GUI_NICKNAME_INPUTNAME_HOVER).create()));
            text.addExtra(inputNick);

            book.addPage(text);
            BookApi.openBook(player, book);
            return true;
        }

        // 处理需要匿名的前缀后缀
        String nickedPrefix = playerNickGuiCache.getNickedPrefix();
        String nickedSuffix = playerNickGuiCache.getNickedSuffix();

        String nickName = playerNickGuiCache.getNickname();
        String playerName = API.getPlayerName(player);
        // 大厅处理
        if (Config.isLobby) {
            // 如果玩家不能在大厅匿名 直接保存数据返回
            if (!player.hasPermission(Permissions.NICK_ON_LOBBY_PERMISSION)) {
                saveNickData(player, playerName, nickName, nickedPrefix, nickedSuffix);
                finishBook(player, nickName);
                player.sendMessage(Messages.NICK_SUCCESSFUL_MESSAGE);
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
        finishBook(player, nickName);

        player.sendMessage(Messages.NICK_SUCCESSFUL_MESSAGE);

        return true;
    }

    private void handleProcess(Player player, String[] args) {
        PlayerData playerNickGuiCache = nickGuiCache.get(player.getUniqueId());
        switch (args[0].toLowerCase()) {
            case "rank":
                String rank = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                playerNickGuiCache.setNickedPrefix(rank);
                player.performCommand("nickbookgui");
                break;
            case "inputname":
                try {
                    SignGUI gui = SignGUI.builder()
                            .setLines(null, "^^^^^^^^^^^^^^^", "在此输入你", "喜欢的昵称")
                            .setType(Material.SIGN_POST)
                            .setColor(DyeColor.YELLOW)
                            .setHandler((p, result) -> {
                                String line0 = result.getLine(0);
                                Bukkit.getScheduler().runTask(GuNick.getPlugin(), () -> {
                                    if (!validNickNameReason(player, line0).isEmpty()) {
                                        player.performCommand("nickbookgui error " + (validNickNameReason(player, line0)));
                                        return;
                                    }
                                    playerNickGuiCache.setNickname(line0);
                                    player.performCommand("nickbookgui");
                                });
                                return Collections.emptyList();
                            })
                            .build();
                    gui.open(player);
                } catch (SignGUIVersionException e) {
                    GuNick.getPlugin().getLogger().severe(Messages.translateCC("&cSignGUI不支持当前Minecraft版本."));
                }
                break;
            default:
        }
    }

    private void finishBook(Player player, String nickedName) {
        Book book = new Book();
        TextComponent finishBook = new TextComponent(Messages.NICK_FINISH.replace("%nickedname%", nickedName));

        book.addPage(finishBook);
        BookApi.openBook(player, book);
    }

    private String validNickNameReason(Player player, String nickName) {
        if (!Config.namePattern.matcher(nickName).matches()) {
            return Messages.NICK_FAIL_CONTAINS_SPECIAL_CHAR_MESSAGE;
        }
        if (nickName.length() < Config.nickMinLength) {
            return Messages.NICK_FAIL_TOO_SHORT_MESSAGE;
        }
        if (nickName.length() > Config.nickLength) {
            return Messages.NICK_FAIL_TOO_LONG_MESSAGE;
        }
        // 如果nickName包含玩家名字
        if (API.getPlayerName(player).equals(nickName)) {
            // 如果玩家匿名了, 使用lib自带的查询要nickname的名字是否是真实名字
            return Messages.NICK_FAIL_AS_SELF_MESSAGE;
        }
        return "";
    }

    private void saveNickData(Player player, String playerName, String nickName, String nickedPrefix, String nickedSuffix) {
        UUID playerUUID = player.getUniqueId();

        if (GuNick.getPlugin().getDataCache().get(playerUUID) != null) {
            PlayerData playerData = GuNick.getPlugin().getDataCache().get(playerUUID);
            playerData.setName(API.getPlayerName(player));
            playerData.setNickname(nickName);
            playerData.setNickedPrefix(nickedPrefix);
            playerData.setNickedSuffix(nickedSuffix);
            API.savePlayerData(player.getUniqueId());

            nickGuiCache.remove(playerUUID);
            return;
        }

        PlayerData playerData = nickGuiCache.get(playerUUID);

        String prefix = LuckPermsUtil.getPrefix(playerUUID);
        String suffix = LuckPermsUtil.getSuffix(playerUUID);
        // 对前缀做处理了还没对后缀做处理, 以后有机会再补
        playerData.setNickedSuffix(nickedSuffix);
        // 补充new playeData后未补齐的匿名前后缀部分
        playerData.setPrefix(prefix);
        playerData.setSuffix(suffix);

        if (!nickedPrefix.isEmpty()) LuckPermsUtil.setPrefix(playerUUID, nickedPrefix);
        if (!nickedSuffix.isEmpty()) LuckPermsUtil.setSuffix(playerUUID, nickedSuffix);
        GuNick.getPlugin().getDataCache().put(player.getUniqueId(), playerData);
        API.savePlayerData(player.getUniqueId());

        nickGuiCache.remove(playerUUID);
    }

    // 没有提供关闭书本事件的API
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getPitch() != to.getPitch() || from.getYaw() != to.getYaw()) {
            nickGuiCache.remove(playerUUID);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        nickGuiCache.remove(playerUUID);
    }

    public boolean isPlayerOnBookGuiCache(UUID playerUUID) {
        return nickGuiCache.get(playerUUID) != null && nickGuiCache.containsKey(playerUUID);
    }
}