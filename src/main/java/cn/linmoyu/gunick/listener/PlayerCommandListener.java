package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.utils.Config;
import cn.linmoyu.gunick.utils.Messages;
import cn.linmoyu.gunick.utils.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerCommandListener implements Listener {

    private final ArrayList<String> vanishCommands = new ArrayList<>(Arrays.asList(
            "/vanish", "/unvanish", "/v", "/yinshen"
    ));

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String fullCommand = event.getMessage();
        if (fullCommand.isEmpty()) return;

        String[] args = fullCommand.split(" ");
        if (!args[0].equalsIgnoreCase("/gunick")) return;

        Player player = event.getPlayer();

        if (args.length > 1 && args[1].equalsIgnoreCase("reload")) {
            if (player.hasPermission(Permissions.NICK_ADMIN_RELOAD_PERMISSION)) {
                event.setCancelled(true);
                Config.reloadConfig(GuNick.getPlugin());
                player.sendMessage(Messages.NICK_RELOAD_MESSAGE);
            }
            return;
        }

        event.setCancelled(true);
        player.sendMessage("§fGuNick By §b@LinMoyu_ | YukiEnd §av" +
                GuNick.getPlugin().getDescription().getVersion());
    }

//    @EventHandler
//    public void onVanishCommand(PlayerCommandPreprocessEvent event) {
//        if (event.getHandlers() == null) return;
//        if (!Config.actionbar_vanishbar) return;
//        String fullCommand = event.getMessage();
//        if (fullCommand.isEmpty()) return;
//        String baseCommand = fullCommand.split(" ")[0];
//
//        if (vanishCommands.stream().noneMatch(baseCommand::startsWith)) return;
//        Player player = event.getPlayer();
//        if (!player.hasPermission(Permissions.NICK_USE_PERMISSION)) return;
//        if (Config.isLobby) {
//            Messages.handleLobbyActionBar(player);
//        }
//    }

}
