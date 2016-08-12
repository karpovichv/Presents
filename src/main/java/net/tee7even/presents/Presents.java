package net.tee7even.presents;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import net.tee7even.presents.provider.DataProvider;
import net.tee7even.presents.provider.YamlProvider;
import net.tee7even.presents.texttag.TextTagManager;

import java.io.File;
import java.util.*;

/**
 * @author Tee7even
 */
public class Presents extends PluginBase
{
    private static Presents instance;

    private DataProvider dataProvider;
    private List<Chest> chests;

    private Map<String, String> messages = new HashMap<>();

    private int totalWeight = 0;
    private Map<Integer, Weight> itemWeights = new HashMap<>();

    private class Weight
    {
        int weight;
        int min;
        int max;

        Weight(int weight, int min, int max)
        {
            this.weight = weight;
            this.min = min;
            this.max = max;
        }
    }

    public static Presents getInstance()
    {
        return instance;
    }

    public Presents()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        this.saveResource("messages.yml", false);
        this.saveResource("weights.yml", false);

        this.dataProvider = new YamlProvider();
        this.chests = dataProvider.loadChests();

        this.loadMessages();
        this.loadWeights();

        TextTagManager.instantiate();

        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable()
    {
        this.dataProvider.close();
        for(Chest chest : this.chests)
            chest.close();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if(player == null) return false;

        if(command.getName().equals("placechest"))
        {
            int[] faces = {4, 2, 5, 3};
            this.chests.add(new Chest(player.getFloorX(), player.getFloorY(), player.getFloorZ(), player.level, faces[player.getDirection()]));
            this.dataProvider.saveChest(player.getFloorX(), player.getFloorY(), player.getFloorZ(), player.level.getName(), faces[player.getDirection()]);

            player.sendMessage(this.getMessage("place"));
        }

        return true;
    }

    public boolean tryOpen(Player player, Block block)
    {
        for(Chest chest : this.chests)
        {
            if(chest.blockEquals(block))
            {
                chest.open(player, getRandomItem());
                return true;
            }
        }

        return false;
    }

    public boolean isChestItemEntity(EntityItem itemEntity)
    {
        for(Chest chest : this.chests)
        {
            if(chest.itemEntityEquals(itemEntity))
                return true;
        }

        return false;
    }

    public boolean tryRemove(Player player, Block block)
    {
        for(Iterator<Chest> i = this.chests.iterator(); i.hasNext();)
        {
            Chest chest = i.next();
            if(chest.blockEquals(block))
            {
                if(player.hasPermission("presents"))
                {
                    chest.close();
                    i.remove();

                    this.dataProvider.removeChest(block.x, block.y, block.z, block.level.getName(), block.getDamage());

                    player.sendMessage(this.getMessage("remove.success"));
                    return true;
                }

                player.sendMessage(this.getMessage("remove.no-permission"));
                return false;
            }
        }

        return true;
    }

    public String getMessage(String key)
    {
        if(messages.containsKey(key))
        {
            return this.messages.get(key);
        }

        return key;
    }

    private Item getRandomItem()
    {
        int sum = 0, num = new Random().nextInt(this.totalWeight);
        for(Map.Entry<Integer, Weight> entry : this.itemWeights.entrySet())
        {
            sum += entry.getValue().weight;
            if(sum >= num)
            {
                return Item.get(entry.getKey(), 0, new Random().nextInt((entry.getValue().max - entry.getValue().min) + 1) + entry.getValue().min);
            }
        }

        return Item.get(0);
    }

    private void loadMessages()
    {
        Config messages = new Config(this.getDataFolder() + File.separator + "messages.yml", Config.YAML);
        for(String key : messages.getKeys(true))
        {
            this.messages.put(key, TextFormat.colorize(messages.getString(key)));
        }
    }

    private void loadWeights()
    {
        Config weightConfig = new Config(this.getDataFolder() + File.separator + "weights.yml", Config.YAML);
        for(String itemId : weightConfig.getKeys(false))
        {
            this.totalWeight += weightConfig.getSection(itemId).getInt("weight");
            this.itemWeights.put(Integer.parseInt(itemId), new Weight(weightConfig.getSection(itemId).getInt("weight"),
                    weightConfig.getSection(itemId).getInt("min"),
                    weightConfig.getSection(itemId).getInt("max")));
        }
    }
}
