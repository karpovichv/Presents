package net.tee7even.presents;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.BlockEventPacket;
import net.tee7even.presents.task.FirstStepTask;
import net.tee7even.presents.task.SecondStepTask;
import net.tee7even.presents.task.ThirdStepTask;
import net.tee7even.presents.utils.Message;
import net.tee7even.presents.utils.TextTagManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tee7even
 */
public class Chest {
    private BlockChest block;

    private Entity itemEntity;
    private int textTagId;

    private boolean inUse = false;
    private Map<String, Long> cooldownTimes = new HashMap<>();

    public Chest(double x, double y, double z, Level level, int facing) {
        block = new BlockChest(facing);
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = level;
        level.setBlock(block, block, true, true);

        new BlockEntityChest(level.getChunk((int)x >> 4, (int)z >> 4), new CompoundTag("")
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.CHEST)
                .putInt("x", (int)x)
                .putInt("y", (int)y)
                .putInt("z", (int)z));
    }

    public void open(Player player, Item item) {
        if (this.inUse) {
            Message.send(player, "in-use");
            return;
        }

        if (!player.getInventory().canAddItem(item)) {
            Message.send(player, "full-inventory");
            return;
        }

        if (Presents.getPlugin().getConfig().getBoolean("cooldown", true)) {
            long unixNow = Instant.now().getEpochSecond();
            if (cooldownTimes.containsKey(player.getName())) {
                if (unixNow - cooldownTimes.get(player.getName()) < Presents.getPlugin().getConfig().getInt("cooldown-time", 300)) {
                    Message.send(player, "cooldown");
                    return;
                }
            }

            cooldownTimes.put(player.getName(), unixNow);
        }

        BlockEventPacket packet = new BlockEventPacket();
        packet.x = (int)block.x;
        packet.y = (int)block.y;
        packet.z = (int)block.z;
        packet.case1 = 1;
        packet.case2 = 2;
        block.getLevel().addChunkPacket((int)block.x >> 4, (int)block.z >> 4, packet);

        inUse = true;
        Server.getInstance().getScheduler().scheduleDelayedTask(new FirstStepTask(this, block, item), 5);
        Server.getInstance().getScheduler().scheduleDelayedTask(new SecondStepTask(this, block, item, player), 10);
        Server.getInstance().getScheduler().scheduleDelayedTask(new ThirdStepTask(this), 200);
    }

    public void setItemEntity(Entity itemEntity) {
        this.itemEntity = itemEntity;
    }

    public void setTextTagId(int textTagId) {
        this.textTagId = textTagId;
    }

    public void close() {
        if (!inUse) {
            return;
        }

        TextTagManager.removeTextTag(textTagId);
        itemEntity.kill();
        inUse = false;

        BlockEventPacket packet = new BlockEventPacket();
        packet.x = (int)block.x;
        packet.y = (int)block.y;
        packet.z = (int)block.z;
        packet.case1 = 1;
        packet.case2 = 0;
        block.getLevel().addChunkPacket((int)block.x >> 4, (int)block.z >> 4, packet);
    }

    public boolean blockEquals(Block block) {
        return this.block != null && this.block.equals(block);
    }

    public boolean itemEntityEquals(EntityItem itemEntity) {
        return this.itemEntity != null && this.itemEntity.equals(itemEntity);
    }
}
