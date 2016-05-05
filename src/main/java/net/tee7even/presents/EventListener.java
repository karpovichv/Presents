package net.tee7even.presents;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.tee7even.presents.texttag.TextTagManager;

/**
 * Created by Tee7even on 25.04.2016.
 */
public class EventListener implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        TextTagManager.getInstance().sendTextTags(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getBlock().getId() != 54)
            return;

        if(event.getAction() != PlayerInteractEvent.RIGHT_CLICK_BLOCK)
            return;

        if(Presents.getInstance().tryOpen(event.getPlayer(), event.getBlock()))
            event.setCancelled();
    }

    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event)
    {
        if(Presents.getInstance().isChestItemEntity(event.getItem()))
            event.setCancelled();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.getBlock().getId() != 54)
            return;

        if(!Presents.getInstance().tryRemove(event.getPlayer(), event.getBlock()))
            event.setCancelled();
    }
}
