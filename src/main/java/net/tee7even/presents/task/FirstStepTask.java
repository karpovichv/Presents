package net.tee7even.presents.task;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.*;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.scheduler.Task;
import net.tee7even.presents.Chest;
import net.tee7even.presents.Presents;

import java.util.List;
import java.util.Random;

/**
 * @author Tee7even
 */
public class FirstStepTask extends Task {
    private static Random rng = new Random();
    private Chest chest;
    private Block block;
    private Item item;

    public FirstStepTask(Chest chest, Block block, Item item) {
        this.chest = chest;
        this.block = block;
        this.item = item;
    }

    @Override
    public void onRun(int currentTick) {
        List<String> particles = Presents.getPlugin().getConfig().getStringList("particles");
        String name = particles.get(rng.nextInt(particles.size()));
        Particle particle;

        switch (name) {
            case "hearts":
                particle = new HeartParticle(new Vector3(0, 0, 0));
                break;
            case "angry-villager":
                particle = new AngryVillagerParticle(new Vector3(0, 0, 0));
                break;
            case "happy-villager":
            default:
                particle = new HappyVillagerParticle(new Vector3(0, 0, 0));
                break;
        }

        for (int i = 0; i < 50; i++) {
            particle.x = block.x + rng.nextDouble();
            particle.y = block.y + rng.nextDouble() + 0.9;
            particle.z = block.z + rng.nextDouble();
            block.getLevel().addParticle(particle);
        }

        Entity itemEntity = shootItem(block.getLevel(), new Vector3(block.x + 0.5, block.y + 0.5, block.z + 0.5), new Vector3(0, 0.3, 0));
        chest.setItemEntity(itemEntity);
    }

    private Entity shootItem(Level level, Vector3 source, Vector3 motion) {
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
                        .put("id", new ShortTag("id", item.getId()))
                        .put("Count", new ByteTag("Count", 1))
                        .put("Damage", new ShortTag("Damage", item.getDamage())))
                .put("PickupDelay", new ShortTag("PickupDelay", 0)));

        itemEntity.spawnToAll();
        return itemEntity;
    }
}
