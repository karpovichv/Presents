package net.tee7even.presents;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import net.tee7even.presents.provider.DataProvider;
import net.tee7even.presents.provider.YamlProvider;
import net.tee7even.presents.utils.Message;

import java.io.File;
import java.util.*;

/**
 * @author Tee7even
 */
class ChestManager {
    private static DataProvider dataProvider;
    private static List<Chest> chests;

    private static int totalWeight = 0;
    private static Map<Integer, Weight> itemWeights = new HashMap<>();
    private static Random rng = new Random();

    private static class Weight {
        int weight;
        int min;
        int max;

        Weight(int weight, int min, int max) {
            this.weight = weight;
            this.min = min;
            this.max = max;
        }
    }

    static void init() {
        dataProvider = new YamlProvider();
        chests = dataProvider.loadChests();
        loadWeights();
        Server.getInstance().getScheduler().scheduleDelayedTask(() -> dataProvider.save(), 5000);
    }

    static void place(Player player) {
        int[] faces = {4, 2, 5, 3};
        chests.add(new Chest(player.getFloorX(), player.getFloorY(), player.getFloorZ(), player.level, faces[player.getDirection()]));
        dataProvider.saveChest(player.getFloorX(), player.getFloorY(), player.getFloorZ(), player.level.getName(), faces[player.getDirection()]);
        Message.send(player, "place");
    }

    static boolean tryOpen(Player player, Block block) {
        for (Chest chest : chests) {
            if (chest.blockEquals(block)) {
                chest.open(player, getRandomItem());
                return true;
            }
        }

        return false;
    }

    static boolean isChestItemEntity(EntityItem itemEntity) {
        for (Chest chest : chests) {
            if (chest.itemEntityEquals(itemEntity)) {
                return true;
            }
        }

        return false;
    }

    static boolean tryRemove(Player player, Block block) {
        for (int i = 0; i < chests.size(); i++) {
            if (chests.get(i).blockEquals(block)) {
                if (player.hasPermission("presents")) {
                    chests.get(i).close();
                    chests.remove(i);

                    dataProvider.removeChest(block.x, block.y, block.z, block.level.getName(), block.getDamage());

                    Message.send(player, "remove.success");
                    return true;
                }

                Message.send(player, "remove.no-permission");
                return false;
            }
        }

        return true;
    }

    static void close() {
        dataProvider.save();
        chests.forEach(Chest::close);
    }

    private static Item getRandomItem() {
        int sum = 0, num = rng.nextInt(totalWeight);
        for (Map.Entry<Integer, Weight> entry : itemWeights.entrySet()) {
            sum += entry.getValue().weight;
            if (sum >= num) {
                return Item.get(entry.getKey(), 0, new Random().nextInt((entry.getValue().max - entry.getValue().min) + 1) + entry.getValue().min);
            }
        }

        return Item.get(0);
    }

    private static void loadWeights() {
        Config weightConfig = new Config(new File(Presents.getPlugin().getDataFolder(), "weights.yml"), Config.YAML);
        for (String itemId : weightConfig.getKeys(false)) {
            totalWeight += weightConfig.getSection(itemId).getInt("weight");
            itemWeights.put(Integer.parseInt(itemId), new Weight(weightConfig.getSection(itemId).getInt("weight"),
                    weightConfig.getSection(itemId).getInt("min"),
                    weightConfig.getSection(itemId).getInt("max")));
        }
    }
}
