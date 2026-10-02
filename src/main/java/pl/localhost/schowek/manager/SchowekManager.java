package pl.localhost.schowek.manager;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.localhost.schowek.Main;
import pl.localhost.schowek.model.SchowekItem;

public class SchowekManager {

    private final Main plugin;

    public SchowekManager(Main plugin) {
        this.plugin = plugin;
    }

    public void checkAndDepositExcess(Player player, boolean sendAlert) {
        if (!plugin.getConfigManager().isAutoSchowekEnabled()) {
            return;
        }

        boolean depositedAny = false;
        StringBuilder alertBuilder = new StringBuilder();

        for (SchowekItem item : plugin.getConfigManager().getItems().values()) {
            int inInv = item.countInInventory(player);
            if (inInv > item.getLimit()) {
                int excess = inInv - item.getLimit();
                int removed = item.removeFromInventory(player, excess);
                if (removed > 0) {
                    plugin.getDataManager().addAmount(player.getUniqueId(), item.getKey(), removed);
                    depositedAny = true;

                    String msg = plugin.getConfigManager().getRawMessage("auto-deposited")
                            .replace("%amount%", String.valueOf(removed))
                            .replace("%item%", item.getDisplayName())
                            .replace("%limit%", String.valueOf(item.getLimit()));

                    if (sendAlert) {
                        player.sendMessage(plugin.getConfigManager().getPrefix() + msg);
                    }
                    if (alertBuilder.length() > 0) alertBuilder.append(" &8| ");
                    alertBuilder.append(item.getDisplayName()).append("&7: &6+").append(removed);
                }
            }
        }

        if (depositedAny) {
            Sound sound = plugin.getConfigManager().getSound("deposit-sound", Sound.ENTITY_ITEM_PICKUP);
            player.playSound(player.getLocation(), sound, 0.7f, 1.2f);

            if (plugin.getConfigManager().isActionBarAlertEnabled() && alertBuilder.length() > 0) {
                String bar = plugin.getConfigManager().color("&8[&6Schowek&8] &7Schowano nadmiar: &f" + alertBuilder);
                player.sendActionBar(bar);
            }
        }
    }

    public boolean withdrawToLimit(Player player, SchowekItem item) {
        int inInv = item.countInInventory(player);
        if (inInv >= item.getLimit()) {
            String msg = plugin.getConfigManager().getMessage("limit-reached")
                    .replace("%item%", item.getDisplayName())
                    .replace("%limit%", String.valueOf(item.getLimit()));
            player.sendMessage(msg);
            return false;
        }

        int inSchowek = plugin.getDataManager().getAmount(player.getUniqueId(), item.getKey());
        if (inSchowek <= 0) {
            String msg = plugin.getConfigManager().getMessage("nothing-to-withdraw")
                    .replace("%item%", item.getDisplayName());
            player.sendMessage(msg);
            return false;
        }

        int needed = item.getLimit() - inInv;
        int toWithdraw = Math.min(needed, inSchowek);
        int added = item.addToInventory(player, toWithdraw);

        if (added <= 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("inventory-full"));
            return false;
        }

        plugin.getDataManager().removeAmount(player.getUniqueId(), item.getKey(), added);

        String msg = plugin.getConfigManager().getMessage("withdrawn")
                .replace("%amount%", String.valueOf(added))
                .replace("%item%", item.getDisplayName());
        player.sendMessage(msg);

        Sound sound = plugin.getConfigManager().getSound("withdraw-sound", Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        player.playSound(player.getLocation(), sound, 0.7f, 1.3f);
        return true;
    }

    public boolean withdrawOne(Player player, SchowekItem item) {
        int inInv = item.countInInventory(player);
        if (inInv >= item.getLimit()) {
            String msg = plugin.getConfigManager().getMessage("limit-reached")
                    .replace("%item%", item.getDisplayName())
                    .replace("%limit%", String.valueOf(item.getLimit()));
            player.sendMessage(msg);
            return false;
        }

        int inSchowek = plugin.getDataManager().getAmount(player.getUniqueId(), item.getKey());
        if (inSchowek <= 0) {
            String msg = plugin.getConfigManager().getMessage("nothing-to-withdraw")
                    .replace("%item%", item.getDisplayName());
            player.sendMessage(msg);
            return false;
        }

        int added = item.addToInventory(player, 1);
        if (added <= 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("inventory-full"));
            return false;
        }

        plugin.getDataManager().removeAmount(player.getUniqueId(), item.getKey(), 1);

        String msg = plugin.getConfigManager().getMessage("withdrawn")
                .replace("%amount%", "1")
                .replace("%item%", item.getDisplayName());
        player.sendMessage(msg);

        Sound sound = plugin.getConfigManager().getSound("withdraw-sound", Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        player.playSound(player.getLocation(), sound, 0.7f, 1.4f);
        return true;
    }

    public boolean depositItem(Player player, SchowekItem item) {
        int inInv = item.countInInventory(player);
        if (inInv <= 0) {
            return false;
        }

        int removed = item.removeFromInventory(player, inInv);
        if (removed > 0) {
            plugin.getDataManager().addAmount(player.getUniqueId(), item.getKey(), removed);

            String msg = plugin.getConfigManager().getMessage("deposited")
                    .replace("%amount%", String.valueOf(removed))
                    .replace("%item%", item.getDisplayName());
            player.sendMessage(msg);

            Sound sound = plugin.getConfigManager().getSound("deposit-sound", Sound.ENTITY_ITEM_PICKUP);
            player.playSound(player.getLocation(), sound, 0.7f, 1.0f);
            return true;
        }
        return false;
    }

    public void restockAllToLimit(Player player) {
        int totalAdded = 0;

        for (SchowekItem item : plugin.getConfigManager().getItems().values()) {
            int inInv = item.countInInventory(player);
            if (inInv < item.getLimit()) {
                int inSchowek = plugin.getDataManager().getAmount(player.getUniqueId(), item.getKey());
                if (inSchowek > 0) {
                    int needed = item.getLimit() - inInv;
                    int toWithdraw = Math.min(needed, inSchowek);
                    int added = item.addToInventory(player, toWithdraw);
                    if (added > 0) {
                        plugin.getDataManager().removeAmount(player.getUniqueId(), item.getKey(), added);
                        totalAdded += added;
                    }
                }
            }
        }

        if (totalAdded > 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("withdrawn-all"));
            Sound sound = plugin.getConfigManager().getSound("withdraw-sound", Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            player.playSound(player.getLocation(), sound, 0.8f, 1.2f);
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("nothing-to-withdraw").replace("%item%", ""));
        }
    }
}
