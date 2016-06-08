package net.tee7even.presents.texttag;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.HashMap;

/**
 * @author Tee7even
 */
public class TextTagManager
{
    private static TextTagManager instance;

    private int textTagCount = 0;
    private HashMap<Integer, TextTag> textTags = new HashMap<>();

    private class TextTag
    {
        String text;

        long eid;
        String levelName;
        double x;
        double y;
        double z;

        public TextTag(String text, String levelName, double x, double y, double z)
        {
            this.text = text;
            this.eid = Entity.entityCount++;
            this.levelName = levelName;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static void instantiate()
    {
        instance = new TextTagManager();
    }

    public static TextTagManager getInstance()
    {
        return instance;
    }

    public int setTextTag(String text, String levelName, double x, double y, double z)
    {
        TextTag textTag = new TextTag(text, levelName, x, y, z);

        AddEntityPacket packet = new AddEntityPacket();
        packet.eid = textTag.eid;
        packet.type = EntityItem.NETWORK_ID;
        packet.x = (float)x;
        packet.y = (float)y;
        packet.z = (float)z;
        packet.speedX = 0;
        packet.speedY = 0;
        packet.speedZ = 0;
        packet.yaw = 0;
        packet.pitch = 0;
        packet.metadata = new EntityMetadata()
                .putByte(Entity.DATA_FLAGS, 1 << Entity.DATA_FLAG_INVISIBLE)
                .putShort(Entity.DATA_AIR, 300)
                .putString(Entity.DATA_NAMETAG, text)
                .putBoolean(Entity.DATA_SHOW_NAMETAG, true)
                .putBoolean(Entity.DATA_SILENT, true)
                .putBoolean(Entity.DATA_NO_AI, true);

        for(Player player : Server.getInstance().getOnlinePlayers().values())
            player.dataPacket(packet);

        textTags.put(textTagCount, textTag);
        return textTagCount++;
    }

    public void removeTextTag(int id)
    {
        if(!textTags.containsKey(id))
            return;

        RemoveEntityPacket packet = new RemoveEntityPacket();
        packet.eid = textTags.get(id).eid;

        for(Player player : Server.getInstance().getOnlinePlayers().values())
            player.dataPacket(packet);

        textTags.remove(id);
    }

    public void sendTextTags(Player player)
    {
        AddEntityPacket packet = new AddEntityPacket();
        packet.type = EntityItem.NETWORK_ID;
        packet.speedX = 0;
        packet.speedY = 0;
        packet.speedZ = 0;
        packet.yaw = 0;
        packet.pitch = 0;
        packet.metadata = new EntityMetadata()
                .putByte(Entity.DATA_FLAGS, 1 << Entity.DATA_FLAG_INVISIBLE)
                .putShort(Entity.DATA_AIR, 300)
                .putBoolean(Entity.DATA_SHOW_NAMETAG, true)
                .putBoolean(Entity.DATA_SILENT, true)
                .putBoolean(Entity.DATA_NO_AI, true);

        for(TextTag textTag : textTags.values())
        {
            if(!player.getLevel().getName().equals(textTag.levelName))
                continue;

            packet.eid = textTag.eid;
            packet.x = (float)textTag.x;
            packet.y = (float)textTag.y;
            packet.z = (float)textTag.z;
            packet.metadata.putString(Entity.DATA_NAMETAG, textTag.text);

            player.dataPacket(packet);
        }
    }
}
