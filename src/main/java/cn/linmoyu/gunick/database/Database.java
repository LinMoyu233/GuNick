package cn.linmoyu.gunick.database;

import java.util.UUID;

public interface Database {

    void init();

    void close();

    PlayerData loadPlayerData(UUID uuid);

    void savePlayerData(PlayerData data);

    void deletePlayerData(UUID uuid);
}