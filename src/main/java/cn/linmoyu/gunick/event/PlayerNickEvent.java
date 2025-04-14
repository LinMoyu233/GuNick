package cn.linmoyu.gunick.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerNickEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    private final String nickName;
    @Getter
    private final String nickedPrefix;
    @Getter
    private final String nickedSuffix;
    private final Boolean needRefresh;
    private Boolean cancelled = false;

    public PlayerNickEvent(Player player, String nickName, boolean needRefresh, String nickedPrefix, String nickedSuffix) {
        this.player = player;
        this.nickName = nickName;
        this.nickedPrefix = nickedPrefix;
        this.nickedSuffix = nickedSuffix;
        this.needRefresh = needRefresh;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    public boolean needRefresh() {
        return this.needRefresh;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}