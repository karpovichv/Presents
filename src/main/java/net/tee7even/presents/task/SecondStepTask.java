package net.tee7even.presents.task;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import net.tee7even.presents.Chest;
import net.tee7even.presents.texttag.TextTagManager;

/**
 * @author Tee7even
 */
public class SecondStepTask extends Task
{
    private Chest chest;
    private Block block;
    private Item item;
    private Player player;

    public SecondStepTask(Chest chest, Block block, Item item, Player player)
    {
        this.chest = chest;
        this.block = block;
        this.item = item;
        this.player = player;
    }

    @Override
    public void onRun(int currentTick)
    {
        int textTagId = TextTagManager.getInstance().setTextTag(this.item.getName(), this.block.getLevel().getName(), this.block.x + 0.5, this.block.y + 1.2, this.block.z + 0.5);

        if(this.player.getInventory().canAddItem(this.item))
        {
            this.player.getInventory().addItem(this.item);
        }

        this.chest.secondStepResult(textTagId);
    }
}
