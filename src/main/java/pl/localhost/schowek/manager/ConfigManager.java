package pl.localhost.schowek.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.localhost.schowek.Main;
import pl.localhost.schowek.model.SchowekItem;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {

    private final Main plugin;
    private final Map<String, SchowekItem> items = new LinkedHashMap<>();

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadItems();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadItems();
    }

    private void loadItems() {
        items.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection sec = config.getConfigurationSection("items");
        if (sec == null) return;

        for (String key : sec.getKeys(false)) {
            String matName = sec.getString(key + ".material", "STONE");
            Material mat = Material.matchMaterial(matName);
            if (mat == null) mat = Material.STONE;

            String name = color(sec.getString(key + ".name", key));
            int limit = sec.getInt(key + ".limit", 1);
            int slot = sec.getInt(key + ".gui-slot", 10 + items.size());

            items.put(key.toLowerCase(), new SchowekItem(key.toLowerCase(), mat, name, limit, slot));
        }
    }

    public String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public List<String> colorList(List<String> list) {
        if (list == null) return new ArrayList<>();
        return list.stream().map(this::color).collect(Collectors.toList());
    }

    public String getPrefix() {
        return color(plugin.getConfig().getString("messages.prefix", "&8[&6Schowek&8] &7"));
    }

    public String getMessage(String path) {
        String msg = plugin.getConfig().getString("messages." + path, "&cWiadomość nieznaleziona: " + path);
        return getPrefix() + color(msg);
    }

    public String getRawMessage(String path) {
        String msg = plugin.getConfig().getString("messages." + path, "");
        return color(msg);
    }

    public boolean isAutoSchowekEnabled() {
        return plugin.getConfig().getBoolean("settings.auto-schowek-enabled", true);
    }

    public boolean isActionBarAlertEnabled() {
        return plugin.getConfig().getBoolean("settings.actionbar-alert", true);
    }

    public int getAutoSaveInterval() {
        return plugin.getConfig().getInt("settings.auto-save-interval", 5);
    }

    public Sound getSound(String path, Sound fallback) {
        String name = plugin.getConfig().getString("settings." + path);
        if (name == null) return fallback;
        try {
            return Sound.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public Map<String, SchowekItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public SchowekItem getItem(String key) {
        if (key == null) return null;
        return items.get(key.toLowerCase());
    }

    public SchowekItem getItemByMaterial(Material mat) {
        for (SchowekItem item : items.values()) {
            if (item.getMaterial() == mat) {
                return item;
            }
        }
        return null;
    }
}
