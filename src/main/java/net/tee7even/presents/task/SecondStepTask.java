package net.tee7even.presents.task;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import net.tee7even.presents.Chest;
import net.tee7even.presents.utils.TextTagManager;

/**
 * @author Tee7even
 */
public class SecondStepTask extends Task {
    private Chest chest;
    private Block block;
    private Item item;
    private Player player;

    public SecondStepTask(Chest chest, Block block, Item item, Player player) {
        this.chest = chest;
        this.block = block;
        this.item = item;
        this.player = player;
    }

    @Override
    public void onRun(int currentTick) {
        int textTagId = TextTagManager.setTextTag(item.getName(), block.getLevel(), block.x + 0.5, block.y + 1, block.z + 0.5);

        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        }

        chest.setTextTagId(textTagId);
    }
}
