package cn.linmoyu.gunick.database;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerData {

    private final UUID uuid;
    private String name;
    private String nickname;
    private String prefix;
    private String suffix;
    private String nickedPrefix;
    private String nickedSuffix;

    public PlayerData(UUID uuid, String name, String nickname,
                      String prefix, String suffix,
                      String nickedPrefix, String nickedSuffix) {
        this.uuid = uuid;
        this.name = name;
        this.nickname = nickname;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nickedPrefix = nickedPrefix;
        this.nickedSuffix = nickedSuffix;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setNickedPrefix(String nickedPrefix) {
        this.nickedPrefix = nickedPrefix;
    }

    public void setNickedSuffix(String nickedSuffix) {
        this.nickedSuffix = nickedSuffix;
    }

}