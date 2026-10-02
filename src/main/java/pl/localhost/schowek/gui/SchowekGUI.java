package pl.localhost.schowek.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.localhost.schowek.Main;
import pl.localhost.schowek.model.SchowekItem;

import java.util.ArrayList;
import java.util.List;

public class SchowekGUI implements Listener {

    private final Main plugin;
    private static final String GUI_IDENTIFIER = "§0";

    public SchowekGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        String baseTitle = plugin.getConfigManager().color(plugin.getConfig().getString("gui.title", "&8Schowek &7(Depozyt PvP)"));
        int size = plugin.getConfig().getInt("gui.size", 36);

        Inventory inv = Bukkit.createInventory(null, size, baseTitle + GUI_IDENTIFIER);

        Material bgMat = Material.matchMaterial(plugin.getConfig().getString("gui.background.material", "GRAY_STAINED_GLASS_PANE"));
        if (bgMat == null) bgMat = Material.GRAY_STAINED_GLASS_PANE;

        ItemStack bgItem = new ItemStack(bgMat);
        ItemMeta bgMeta = bgItem.getItemMeta();
        if (bgMeta != null) {
            bgMeta.setDisplayName(plugin.getConfigManager().color(plugin.getConfig().getString("gui.background.name", " ")));
            bgItem.setItemMeta(bgMeta);
        }

        for (int i = 0; i < size; i++) {
            inv.setItem(i, bgItem);
        }

        for (SchowekItem item : plugin.getConfigManager().getItems().values()) {
            if (item.getGuiSlot() >= 0 && item.getGuiSlot() < size) {
                inv.setItem(item.getGuiSlot(), buildItemIcon(player, item));
            }
        }

        int restockSlot = plugin.getConfig().getInt("gui.restock-button.slot", 22);
        if (restockSlot >= 0 && restockSlot < size) {
            Material mat = Material.matchMaterial(plugin.getConfig().getString("gui.restock-button.material", "EMERALD_BLOCK"));
            if (mat == null) mat = Material.EMERALD_BLOCK;
            ItemStack restockItem = new ItemStack(mat);
            ItemMeta meta = restockItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getConfigManager().color(plugin.getConfig().getString("gui.restock-button.name", "&a&lDOBIERZ WSZYSTKO")));
                List<String> rawLore = plugin.getConfig().getStringList("gui.restock-button.lore");
                meta.setLore(plugin.getConfigManager().colorList(rawLore));
                restockItem.setItemMeta(meta);
            }
            inv.setItem(restockSlot, restockItem);
        }

        int depositSlot = plugin.getConfig().getInt("gui.deposit-button.slot", 21);
        if (depositSlot >= 0 && depositSlot < size) {
            Material mat = Material.matchMaterial(plugin.getConfig().getString("gui.deposit-button.material", "HOPPER"));
            if (mat == null) mat = Material.HOPPER;
            ItemStack depositItem = new ItemStack(mat);
            ItemMeta meta = depositItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getConfigManager().color(plugin.getConfig().getString("gui.deposit-button.name", "&c&lSCHOWAJ NADMIAR")));
                List<String> rawLore = plugin.getConfig().getStringList("gui.deposit-button.lore");
                meta.setLore(plugin.getConfigManager().colorList(rawLore));
                depositItem.setItemMeta(meta);
            }
            inv.setItem(depositSlot, depositItem);
        }

        int infoSlot = plugin.getConfig().getInt("gui.info-button.slot", 23);
        if (infoSlot >= 0 && infoSlot < size) {
            Material mat = Material.matchMaterial(plugin.getConfig().getString("gui.info-button.material", "NETHER_STAR"));
            if (mat == null) mat = Material.NETHER_STAR;
            ItemStack infoItem = new ItemStack(mat);
            ItemMeta meta = infoItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getConfigManager().color(plugin.getConfig().getString("gui.info-button.name", "&6&lInformacje")));
                List<String> rawLore = plugin.getConfig().getStringList("gui.info-button.lore");
                meta.setLore(plugin.getConfigManager().colorList(rawLore));
                infoItem.setItemMeta(meta);
            }
            inv.setItem(infoSlot, infoItem);
        }

        player.openInventory(inv);

        Sound sound = plugin.getConfigManager().getSound("gui-sound", Sound.UI_BUTTON_CLICK);
        player.playSound(player.getLocation(), sound, 0.7f, 1.0f);
    }

    private ItemStack buildItemIcon(Player player, SchowekItem item) {
        int inInv = item.countInInventory(player);
        int inSchowek = plugin.getDataManager().getAmount(player.getUniqueId(), item.getKey());

        ItemStack stack = new ItemStack(item.getMaterial());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(item.getDisplayName());

            List<String> lore = new ArrayList<>();
            lore.add(plugin.getConfigManager().color("&7Limit w ekwipunku: &e" + item.getLimit()));
            lore.add(plugin.getConfigManager().color("&7Posiadasz w ekwipunku: &b" + inInv + " &7/ &b" + item.getLimit()));
            lore.add(plugin.getConfigManager().color("&7W schowku: &a" + inSchowek + " szt."));
            lore.add("");
            lore.add(plugin.getConfigManager().color("&8» &eLPM &7- Dobierz do limitu"));
            lore.add(plugin.getConfigManager().color("&8» &ePPM &7- Wypłać 1 sztukę"));
            lore.add(plugin.getConfigManager().color("&8» &eShift + LPM &7- Wpłać ten przedmiot"));

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private void refreshItems(Player player, Inventory inv) {
        int size = inv.getSize();
        for (SchowekItem item : plugin.getConfigManager().getItems().values()) {
            if (item.getGuiSlot() >= 0 && item.getGuiSlot() < size) {
                inv.setItem(item.getGuiSlot(), buildItemIcon(player, item));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().endsWith(GUI_IDENTIFIER)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        int restockSlot = plugin.getConfig().getInt("gui.restock-button.slot", 22);
        int depositSlot = plugin.getConfig().getInt("gui.deposit-button.slot", 21);
        int infoSlot = plugin.getConfig().getInt("gui.info-button.slot", 23);

        if (slot == restockSlot) {
            plugin.getSchowekManager().restockAllToLimit(player);
            refreshItems(player, event.getInventory());
            return;
        }

        if (slot == depositSlot) {
            plugin.getSchowekManager().checkAndDepositExcess(player, true);
            refreshItems(player, event.getInventory());
            return;
        }

        if (slot == infoSlot) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.5f);
            return;
        }

        for (SchowekItem item : plugin.getConfigManager().getItems().values()) {
            if (item.getGuiSlot() == slot) {
                if (event.isShiftClick()) {
                    plugin.getSchowekManager().depositItem(player, item);
                } else if (event.isRightClick()) {
                    plugin.getSchowekManager().withdrawOne(player, item);
                } else {
                    plugin.getSchowekManager().withdrawToLimit(player, item);
                }
                refreshItems(player, event.getInventory());
                break;
            }
        }
    }
}
