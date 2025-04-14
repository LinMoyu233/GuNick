package cn.linmoyu.gunick.database;

import java.util.UUID;

public interface Database {

    void init();

    void close();

    String getPlayerNick(UUID uuid);

    String getPlayerName(UUID uuid);

    void setPlayerNick(UUID uuid, String playerName, String nickname, String prefix, String suffix, String nickedPrefix, String nickedSuffix);

    void clearNick(UUID uuid);

    String getPlayerPrefix(UUID uuid);

    String getPlayerSuffix(UUID uuid);

    String getPlayerNickedPrefix(UUID uuid);

    String getPlayerNickSuffix(UUID uuid);
}