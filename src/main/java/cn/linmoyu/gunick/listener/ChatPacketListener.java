package cn.linmoyu.gunick.listener;

import cn.linmoyu.gunick.GuNick;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import java.util.Map;

public class ChatPacketListener extends PacketAdapter {

    public ChatPacketListener(GuNick plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if (e.getPacket().getType() != PacketType.Play.Server.CHAT) return;

        WrappedChatComponent chat = e.getPacket().getChatComponents().read(0);
        String originalJson = chat.getJson();

        String modifiedJson = originalJson;
        for (Map.Entry<String, String> entry : GuNick.getNickPlayersName().entrySet()) {
            String originalId = entry.getKey();
            String nickId = entry.getValue();
            modifiedJson = modifiedJson.replace(originalId, nickId);
        }

        if (!modifiedJson.equals(originalJson)) {
            e.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(modifiedJson));
        }
    }

}
