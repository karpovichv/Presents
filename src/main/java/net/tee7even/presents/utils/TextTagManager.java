package net.tee7even.presents.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Tee7even
 */
public class TextTagManager implements Listener {
    private static int textTagCount = 0;
    private static HashMap<Integer, TextTag> textTags = new HashMap<>();

    private static class TextTag {
        String text;

        long eid;
        String levelName;
        double x;
        double y;
        double z;

        public TextTag(String text, String levelName, double x, double y, double z) {
            this.text = text;
            this.eid = Entity.entityCount++;
            this.levelName = levelName;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static int setTextTag(String text, Level level, double x, double y, double z) {
        TextTag textTag = new TextTag(text, level.getName(), x, y, z);

        AddEntityPacket packet = new AddEntityPacket();
        packet.type = EntityItem.NETWORK_ID;
        packet.entityRuntimeId = textTag.eid;
        packet.entityUniqueId = textTag.eid;
        packet.x = (float) textTag.x;
        packet.y = (float) textTag.y;
        packet.z = (float) textTag.z;
        packet.speedX = 0;
        packet.speedY = 0;
        packet.speedZ = 0;

        long flags = 0;
        flags |= 1 << Entity.DATA_FLAG_INVISIBLE;
        flags |= 1 << Entity.DATA_FLAG_NO_AI;
        flags |= 1 << Entity.DATA_FLAG_CAN_SHOW_NAMETAG;
        flags |= 1 << Entity.DATA_FLAG_ALWAYS_SHOW_NAMETAG;
        packet.metadata = new EntityMetadata()
                .putLong(Entity.DATA_FLAGS, flags)
                .putString(Entity.DATA_NAMETAG, text)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putByte(Entity.DATA_LEAD, 0);

        level.addChunkPacket((int)textTag.x >> 4, (int)textTag.z >> 4, packet);

        textTags.put(textTagCount, textTag);
        return textTagCount++;
    }

    public static void removeTextTag(int id) {
        if (!textTags.containsKey(id)) {
            return;
        }

        RemoveEntityPacket packet = new RemoveEntityPacket();
        packet.eid = textTags.get(id).eid;

        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            player.dataPacket(packet);
        }

        textTags.remove(id);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.type = EntityItem.NETWORK_ID;
        packet.speedX = 0;
        packet.speedY = 0;
        packet.speedZ = 0;

        long flags = 0;
        flags |= 1 << Entity.DATA_FLAG_INVISIBLE;
        flags |= 1 << Entity.DATA_FLAG_NO_AI;
        flags |= 1 << Entity.DATA_FLAG_CAN_SHOW_NAMETAG;
        flags |= 1 << Entity.DATA_FLAG_ALWAYS_SHOW_NAMETAG;
        packet.metadata = new EntityMetadata()
                .putLong(Entity.DATA_FLAGS, flags)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putByte(Entity.DATA_LEAD, 0);

        for (TextTag textTag : textTags.values()) {
            if (!event.getPlayer().getLevel().getName().equals(textTag.levelName)) {
                continue;
            }

            packet.entityRuntimeId = textTag.eid;
            packet.entityUniqueId = textTag.eid;
            packet.x = (float) textTag.x;
            packet.y = (float) textTag.y;
            packet.z = (float) textTag.z;
            packet.metadata.putString(Entity.DATA_NAMETAG, textTag.text);

            event.getPlayer().dataPacket(packet);
        }
    }
}
