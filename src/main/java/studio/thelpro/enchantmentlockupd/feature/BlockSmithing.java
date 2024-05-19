package studio.thelpro.enchantmentlockupd.feature;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import studio.thelpro.enchantmentlockupd.EnchantmentLockUpd;

/**
 * Updated to support minecraft 1.20.1.
 * @author Newt
 */
public class BlockSmithing implements Listener {

    private final EnchantmentLockUpd enchantmentLock;

    /**
     * Constructor for the BlockSmithing Listener.
     * @param enchantmentLock Instance of the main class.
     */
    public BlockSmithing(EnchantmentLockUpd enchantmentLock) {
        this.enchantmentLock = enchantmentLock;
    }

    /**
     * Smithing event.
     * @param event The respective event listened to.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

//        System.out.println("Detected Event");

        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player)) return;

        Player player = (Player) human;
        if (player.hasPermission("el.bypass")) return;
        Inventory inventory = event.getClickedInventory();
        if (!(inventory instanceof SmithingInventory)) return;

//        System.out.println("Detected Inventory");

        InventoryView inventoryView = event.getView();
        int slot = event.getRawSlot();
        if (slot != inventoryView.convertSlot(slot)) return;

//        System.out.println("Detected Slot");

        ItemStack template = inventory.getItem(0);
        ItemStack item = inventory.getItem(1);
        ItemStack mergedWith = inventory.getItem(2);
        ItemStack result = inventory.getItem(3);

        boolean involvesLockedItem = false;

        if (template == null) return;
        if (item == null) return;
        if (result == null) return;
        if (mergedWith == null) return;

//        System.out.println("Checked slots");

        if (enchantmentLock.itemManager.isLockedItem(template)) involvesLockedItem = true;
        if (enchantmentLock.itemManager.isLockedItem(item)) involvesLockedItem = true;
        if (enchantmentLock.itemManager.isLockedItem(result)) involvesLockedItem = true;
        if (enchantmentLock.itemManager.isLockedItem(mergedWith)) involvesLockedItem = true;

        if (!involvesLockedItem) return;

        event.setCancelled(true);
        player.sendMessage(enchantmentLock.messageManager.cannot_smith);
    }
}
