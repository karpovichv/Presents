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
import net.tee7even.presents.texttag.TextTagManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tee7even
 */
public class Chest
{
    private BlockChest block;

    private Entity itemEntity;
    private int textTagId;

    private boolean inUse = false;
    private Map<String, Long> cooldownTimes = new HashMap<>();

    public Chest(double x, double y, double z, Level level, int facing)
    {
        this.block = new BlockChest(facing);
        this.block.x = x;
        this.block.y = y;
        this.block.z = z;
        this.block.level = level;
        level.setBlock(new Vector3(x, y, z), this.block);

        new BlockEntityChest(level.getChunk((int)x >> 4, (int)z >> 4), new CompoundTag("")
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.CHEST)
                .putInt("x", (int)x)
                .putInt("y", (int)y)
                .putInt("z", (int)z));
    }

    public void open(Player player, Item item)
    {
        if(this.inUse)
        {
            player.sendMessage(Presents.getInstance().getMessage("in-use"));
            return;
        }

        if(!player.getInventory().canAddItem(item))
        {
            player.sendMessage(Presents.getInstance().getMessage("full-inventory"));
            return;
        }

        if(Presents.getInstance().getConfig().getBoolean("cooldown", true))
        {
            long unixNow = Instant.now().getEpochSecond();
            if(this.cooldownTimes.containsKey(player.getName()))
            {
                if(unixNow - this.cooldownTimes.get(player.getName()) < Presents.getInstance().getConfig().getInt("cooldown-time", 300))
                {
                    player.sendMessage(Presents.getInstance().getMessage("cooldown"));
                    return;
                }
            }

            this.cooldownTimes.put(player.getName(), unixNow);
        }

        BlockEventPacket packet = new BlockEventPacket();
        packet.x = (int)this.block.x;
        packet.y = (int)this.block.y;
        packet.z = (int)this.block.z;
        packet.case1 = 1;
        packet.case2 = 2;
        this.block.getLevel().addChunkPacket((int)this.block.x >> 4, (int)this.block.z >> 4, packet);

        this.inUse = true;
        Server.getInstance().getScheduler().scheduleDelayedTask(new FirstStepTask(this, this.block, item), 5);
        Server.getInstance().getScheduler().scheduleDelayedTask(new SecondStepTask(this, this.block, item, player), 10);
        Server.getInstance().getScheduler().scheduleDelayedTask(new ThirdStepTask(this), 200);
    }

    public void firstStepResult(Entity itemEntity)
    {
        this.itemEntity = itemEntity;
    }

    public void secondStepResult(int textTagId)
    {
        this.textTagId = textTagId;
    }

    public void close()
    {
        if(!this.inUse) return;

        TextTagManager.getInstance().removeTextTag(this.textTagId);
        this.itemEntity.kill();
        this.inUse = false;

        BlockEventPacket packet = new BlockEventPacket();
        packet.x = (int)this.block.x;
        packet.y = (int)this.block.y;
        packet.z = (int)this.block.z;
        packet.case1 = 1;
        packet.case2 = 0;
        block.getLevel().addChunkPacket((int)this.block.x >> 4, (int)this.block.z >> 4, packet);
    }

    public boolean blockEquals(Block block)
    {
        return this.block != null && this.block.equals(block);
    }

    public boolean itemEntityEquals(EntityItem itemEntity) {
        return this.itemEntity != null && this.itemEntity.equals(itemEntity);
    }
}
