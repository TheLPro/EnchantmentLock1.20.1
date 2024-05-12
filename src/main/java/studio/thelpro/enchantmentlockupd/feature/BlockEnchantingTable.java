package studio.thelpro.enchantmentlockupd.feature;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import studio.thelpro.enchantmentlockupd.EnchantmentLockUpd;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Updated to support minecraft 1.20.1.
 * @author Newt
 */
public class BlockEnchantingTable implements Listener {

    private final EnchantmentLockUpd enchantmentLock;
    private final List<UUID> cancelDoubleMessage;

    /**
     * Constructor for the BlockEnchantingTable Listener.
     * @param enchantmentLock Instance of the main class.
     */
    public BlockEnchantingTable(EnchantmentLockUpd enchantmentLock) {
        this.enchantmentLock = enchantmentLock;
        this.cancelDoubleMessage = new ArrayList<>();
    }

    /**
     * Enchanting Table event.
     * @param event The respective event listened to.
     */
    @EventHandler
    public void onEnchantmentTable(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();
        if (!enchantmentLock.itemManager.isLockedItem(item)) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getEnchanter();
        if (player.hasPermission("el.bypass")) return;
        UUID uuid = player.getUniqueId();

        // This event is called multiple times for the same enchantment.
        // This prevents the chat of players from being spammed in that case.
        if (!cancelDoubleMessage.contains(uuid)) {
            player.sendMessage(enchantmentLock.messageManager.cannot_enchant);
            cancelDoubleMessage.add(uuid);
            removeInTwoTicks(uuid);
        }
    }

    /**
     * Schedules the removal from a UUID from the cancelDoubleMessage list.
     * @param uuid The UUID to remove.
     */
    private void removeInTwoTicks(UUID uuid) {
        BukkitScheduler scheduler = enchantmentLock.getServer().getScheduler();
        scheduler.runTaskLater(enchantmentLock, () -> cancelDoubleMessage.remove(uuid), 2L);
    }
}
