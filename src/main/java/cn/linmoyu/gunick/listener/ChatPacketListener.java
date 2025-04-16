package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import cn.linmoyu.gunick.utils.Config;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class ChatPacketListener extends PacketAdapter {

    public ChatPacketListener(GuNick plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if (!Config.isJoinNickAsyncAndReplaceMessage) return;
        if (e.getPacket().getMeta("GuNick").isPresent()) {
            return;
        }
        if (e.getPacketType() != PacketType.Play.Server.CHAT) return;

        PacketContainer originalPacket = e.getPacket();
        WrappedChatComponent originalChat = originalPacket.getChatComponents().read(0);
        String originalJson = originalChat.getJson();
        if (!originalJson.contains(Config.joinMessageContain)) return;

        e.setCancelled(true);

        Player player = e.getPlayer();

        Bukkit.getScheduler().runTaskLater(GuNick.getPlugin(), () -> {
            if (!player.isOnline()) return;

            String modifiedJson = originalJson;
            for (Map.Entry<String, String> entry : GuNick.getNickPlayersName().entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
                modifiedJson = modifiedJson.replace(entry.getKey(), entry.getValue());
            }

            PacketContainer newPacket = originalPacket.deepClone();
            newPacket.getChatComponents().write(0, WrappedChatComponent.fromJson(modifiedJson));
            newPacket.setMeta("GuNick", true);

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, newPacket);
        }, Config.joinMessageDelay);
    }

}
