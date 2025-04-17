package cn.linmoyu.gunick.utils;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.database.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.UUID;

public class API {

    public static boolean isPlayerNicked(Player player) {
        return GuNick.getPlugin().getDisguiseProvider().isDisguised(player);
    }

    public static String getPlayerName(Player player) {
        return GuNick.getPlugin().getDisguiseProvider().getInfo(player).getName();
    }

    public static boolean isPlayerDataNicked(Player player) {
        PlayerData playerData = GuNick.getPlugin().getDataCache().get(player.getUniqueId());
        return playerData != null && playerData.getNickname() != null && !playerData.getNickname().isEmpty();
    }

    public static void deletePlayerNickFromDatabase(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
            GuNick.getPlugin().getDataCache().remove(uuid);
            GuNick.getRemoteDatabase().deletePlayerData(uuid);
        });
    }

    public static void savePlayerData(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> {
            PlayerData playerData = GuNick.getPlugin().getDataCache().get(uuid);
            GuNick.getRemoteDatabase().savePlayerData(playerData);
        });
    }

    public static boolean isPlayerVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

}
