package net.tee7even.presents;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;

/**
 * @author Tee7even
 */
public class ChestListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getBlock().getId() != 54) {
            return;
        }

        if (event.getAction() != PlayerInteractEvent.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (ChestManager.tryOpen(event.getPlayer(), event.getBlock())) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event) {
        if (ChestManager.isChestItemEntity(event.getItem())) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getId() != 54) {
            return;
        }

        if (!ChestManager.tryRemove(event.getPlayer(), event.getBlock())) {
            event.setCancelled();
        }
    }
}
