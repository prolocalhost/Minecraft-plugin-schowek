package pl.localhost.schowek.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import pl.localhost.schowek.Main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private final Main plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Map<String, Integer>> storage = new ConcurrentHashMap<>();
    private BukkitTask autoSaveTask;

    public DataManager(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data" + File.separator + "players.yml");
        loadDataFile();
    }

    private void loadDataFile() {
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Nie udalo sie utworzyc pliku players.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public synchronized void loadPlayer(UUID uuid) {
        Map<String, Integer> playerMap = new HashMap<>();
        String path = uuid.toString();

        if (dataConfig.contains(path)) {
            ConfigurationSection sec = dataConfig.getConfigurationSection(path);
            if (sec != null) {
                for (String key : sec.getKeys(false)) {
                    playerMap.put(key.toLowerCase(), sec.getInt(key, 0));
                }
            }
        }
        storage.put(uuid, playerMap);
    }

    public synchronized void savePlayer(UUID uuid) {
        Map<String, Integer> playerMap = storage.get(uuid);
        if (playerMap == null) return;

        String path = uuid.toString();
        for (Map.Entry<String, Integer> entry : playerMap.entrySet()) {
            dataConfig.set(path + "." + entry.getKey(), entry.getValue());
        }
    }

    public synchronized void saveAll() {
        for (UUID uuid : storage.keySet()) {
            savePlayer(uuid);
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Blad podczas zapisu players.yml: " + e.getMessage());
        }
    }

    public int getAmount(UUID uuid, String itemKey) {
        Map<String, Integer> playerMap = storage.get(uuid);
        if (playerMap == null) {
            loadPlayer(uuid);
            playerMap = storage.get(uuid);
        }
        return playerMap != null ? playerMap.getOrDefault(itemKey.toLowerCase(), 0) : 0;
    }

    public void setAmount(UUID uuid, String itemKey, int amount) {
        storage.computeIfAbsent(uuid, k -> new HashMap<>()).put(itemKey.toLowerCase(), Math.max(0, amount));
    }

    public void addAmount(UUID uuid, String itemKey, int amount) {
        int current = getAmount(uuid, itemKey);
        setAmount(uuid, itemKey, current + amount);
    }

    public boolean removeAmount(UUID uuid, String itemKey, int amount) {
        int current = getAmount(uuid, itemKey);
        if (current < amount) {
            return false;
        }
        setAmount(uuid, itemKey, current - amount);
        return true;
    }

    public void startAutoSaveTask() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
        int intervalMinutes = plugin.getConfigManager().getAutoSaveInterval();
        long intervalTicks = intervalMinutes * 60L * 20L;

        autoSaveTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            saveAll();
            plugin.getLogger().info("Automatycznie zapisano dane schowka do players.yml.");
        }, intervalTicks, intervalTicks);
    }

    public void stopAutoSaveTask() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }
        saveAll();
    }
}
