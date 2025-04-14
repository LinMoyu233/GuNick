package cn.linmoyu.gunick.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnNickEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Player player;
    private final Boolean needRefresh;
    private Boolean cancelled = false;

    public PlayerUnNickEvent(Player player, Boolean needRefresh) {
        this.player = player;
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