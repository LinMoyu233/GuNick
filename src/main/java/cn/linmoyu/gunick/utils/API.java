package cn.linmoyu.gunick.utils;

import cn.linmoyu.gunick.GuNick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.UUID;

public class API {

    public static boolean isPlayerNickedDataBase(UUID uuid) {
        String nickName = getPlayerNick(uuid);
        return nickName != null && !nickName.isEmpty();
    }

    public static boolean isPlayerNicked(Player player) {
        return GuNick.getPlugin().getDisguiseProvider().isDisguised(player);
    }

    public static String getPlayerName(Player player) {
        return GuNick.getPlugin().getDisguiseProvider().getInfo(player).getName();
    }

    public static String getPlayerNick(UUID uuid) {
        return GuNick.getRemoteDatabase().getPlayerNick(uuid);
    }

    public static String getPlayerNameFromDatabase(UUID uuid) {
        return GuNick.getRemoteDatabase().getPlayerName(uuid);
    }

    public static String getPlayerPrefix(UUID uuid) {
        return GuNick.getRemoteDatabase().getPlayerPrefix(uuid);
    }

    public static String getPlayerSuffix(UUID uuid) {
        return GuNick.getRemoteDatabase().getPlayerSuffix(uuid);
    }

    public static String getPlayerNickedPrefix(UUID uuid) {
        return GuNick.getRemoteDatabase().getPlayerNickedPrefix(uuid);
    }

    public static String getPlayerNickSuffix(UUID uuid) {
        return GuNick.getRemoteDatabase().getPlayerNickSuffix(uuid);
    }

    public static void setPlayerNickToDatabase(UUID uuid, String playerName, String nickName, String prefix, String suffix, String nickedPrefix, String nickedSuffix) {
        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> GuNick.getRemoteDatabase().setPlayerNick(uuid, playerName, nickName, prefix, suffix, nickedPrefix, nickedSuffix));
    }

    public static void deletePlayerNickFromDatabase(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(GuNick.getPlugin(), () -> GuNick.getRemoteDatabase().clearNick(uuid));
    }

    public static boolean isPlayerVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

}
