package pl.localhost.schowek.model;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SchowekItem {

    private final String key;
    private final Material material;
    private final String displayName;
    private final int limit;
    private final int guiSlot;

    public SchowekItem(String key, Material material, String displayName, int limit, int guiSlot) {
        this.key = key;
        this.material = material;
        this.displayName = displayName;
        this.limit = limit;
        this.guiSlot = guiSlot;
    }

    public String getKey() {
        return key;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLimit() {
        return limit;
    }

    public int getGuiSlot() {
        return guiSlot;
    }

    public int countInInventory(Player player) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public int removeFromInventory(Player player, int amountToRemove) {
        int removed = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                int count = item.getAmount();
                int needed = amountToRemove - removed;

                if (count <= needed) {
                    player.getInventory().setItem(i, null);
                    removed += count;
                } else {
                    item.setAmount(count - needed);
                    removed += needed;
                }

                if (removed >= amountToRemove) {
                    break;
                }
            }
        }
        return removed;
    }

    public int addToInventory(Player player, int amountToAdd) {
        ItemStack item = new ItemStack(material, amountToAdd);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        if (leftover.isEmpty()) {
            return amountToAdd;
        } else {
            int notAdded = 0;
            for (ItemStack left : leftover.values()) {
                notAdded += left.getAmount();
            }
            return amountToAdd - notAdded;
        }
    }
}
