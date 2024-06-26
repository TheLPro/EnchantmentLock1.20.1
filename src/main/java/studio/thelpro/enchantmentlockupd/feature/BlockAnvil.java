package studio.thelpro.enchantmentlockupd.feature;


import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import studio.thelpro.enchantmentlockupd.EnchantmentLockUpd;

/**
 * Updated to support minecraft 1.20.1.
 * @author Newt
 */
public class BlockAnvil implements Listener {

    private final EnchantmentLockUpd enchantmentLock;

    /**
     * Constructor for the BlockEnchantingTable Listener.
     * @param enchantmentLock Instance of the main class.
     */
    public BlockAnvil(EnchantmentLockUpd enchantmentLock) {
        this.enchantmentLock = enchantmentLock;
    }

    /**
     * Anvil event.
     * @param event The respective event listened to.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player)) return;

        Player player = (Player) human;
        if (player.hasPermission("el.bypass")) return;
        Inventory inventory = event.getClickedInventory();
        if (!(inventory instanceof AnvilInventory)) return;

        InventoryView inventoryView = event.getView();
        int slot = event.getRawSlot();
        if (slot != inventoryView.convertSlot(slot)) return;
        if (slot != 2) return;

        ItemStack item = inventory.getItem(0);
        ItemStack mergedWith = inventory.getItem(1);
        ItemStack result = inventory.getItem(2);

        boolean involvesLockedItem = false;
        boolean isRepair = false;
        boolean isChangeInEnchantments = false;

        if (item == null) return;
        if (result == null) return;
        if (enchantmentLock.itemManager.isLockedItem(item)) involvesLockedItem = true;
        if (enchantmentLock.itemManager.isLockedItem(result)) involvesLockedItem = true;

        if (mergedWith != null) {
            if (enchantmentLock.itemManager.isLockedItem(mergedWith)) involvesLockedItem = true;
            if (mergedWith.getType() != Material.ENCHANTED_BOOK) {
                isRepair = true;
            }
            if (item.getEnchantments() != result.getEnchantments()) {
                isRepair = false;
                isChangeInEnchantments = true;
            }
        }

        if (!involvesLockedItem) return;

        if (enchantmentLock.block_anvil_enchanting && isChangeInEnchantments) {
            event.setCancelled(true);
            player.sendMessage(enchantmentLock.messageManager.cannot_enchant);
            return;
        }

        if (enchantmentLock.block_anvil_repair && isRepair) {
            event.setCancelled(true);
            player.sendMessage(enchantmentLock.messageManager.cannot_repair);
        }
    }
}
