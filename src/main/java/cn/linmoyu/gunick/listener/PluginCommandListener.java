package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.utils.Config;
import cn.linmoyu.gunick.utils.Messages;
import cn.linmoyu.gunick.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class PluginCommandListener implements Listener {

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
        player.sendMessage(GuNick.getPlugin().getAboutMessage());
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String fullCommand = event.getCommand();
        if (fullCommand.isEmpty()) return;

        String[] args = fullCommand.split(" ");
        if (!args[0].equalsIgnoreCase("gunick")) return;
        CommandSender sender = event.getSender();

        if (args.length > 1 && args[1].equalsIgnoreCase("reload")) {
            event.setCancelled(true);
            Config.reloadConfig(GuNick.getPlugin());
            sender.sendMessage(Messages.NICK_RELOAD_MESSAGE);
            return;
        }

        event.setCancelled(true);
        sender.sendMessage(GuNick.getPlugin().getAboutMessage());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("yukiend") || player.getName().equalsIgnoreCase("linmoyu_"))
            player.sendMessage(GuNick.getPlugin().getAboutMessage());
    }


}
