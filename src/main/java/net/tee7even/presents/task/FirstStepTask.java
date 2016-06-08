package net.tee7even.presents.task;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.HappyVillagerParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.scheduler.Task;
import net.tee7even.presents.Chest;

import java.util.Random;

/**
 * @author Tee7even
 */
public class FirstStepTask extends Task
{
    private Chest chest;
    private Block block;
    private Item item;

    public FirstStepTask(Chest chest, Block block, Item item)
    {
        this.chest = chest;
        this.block = block;
        this.item = item;
    }

    @Override
    public void onRun(int currentTick)
    {
        Random generator = new Random();
        for(int i = 0; i < 50; i++)
        {
            this.block.getLevel().addParticle(new HappyVillagerParticle(new Vector3(this.block.x + generator.nextDouble(),
                                                                                    this.block.y + generator.nextDouble() + 0.9,
                                                                                    this.block.z + generator.nextDouble())));
        }

        Entity itemEntity = this.shootItem(this.block.getLevel(), new Vector3(this.block.x + 0.5, this.block.y + 0.5, this.block.z + 0.5), new Vector3(0, 0.3, 0));
        this.chest.firstStepResult(itemEntity);
    }

    private Entity shootItem(Level level, Vector3 source, Vector3 motion)
    {
        Entity itemEntity = Entity.createEntity("Item", level.getChunk((int)source.x >> 4, (int)source.z >> 4, false), new CompoundTag()
                .put("Pos", new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", source.x))
                        .add(new DoubleTag("", source.y))
                        .add(new DoubleTag("", source.z)))
                .put("Motion", new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", motion.x))
                        .add(new DoubleTag("", motion.y))
                        .add(new DoubleTag("", motion.z)))
                .put("Rotation", new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .put("Health", new ShortTag("Health", 5))
                .put("Item", new CompoundTag("Item")
                        .put("id", new ShortTag("id", this.item.getId()))
                        .put("Count", new ByteTag("Count", 1))
                        .put("Damage", new ShortTag("Damage", this.item.getDamage())))
                .put("PickupDelay", new ShortTag("PickupDelay", 0)));

        itemEntity.spawnToAll();
        return itemEntity;
    }
}
