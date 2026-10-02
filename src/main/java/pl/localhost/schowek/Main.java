package pl.localhost.schowek;

import org.bukkit.plugin.java.JavaPlugin;
import pl.localhost.schowek.command.SchowekCommand;
import pl.localhost.schowek.gui.SchowekGUI;
import pl.localhost.schowek.listener.SchowekListener;
import pl.localhost.schowek.manager.ConfigManager;
import pl.localhost.schowek.manager.DataManager;
import pl.localhost.schowek.manager.SchowekManager;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private DataManager dataManager;
    private SchowekManager schowekManager;
    private SchowekGUI schowekGUI;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.schowekManager = new SchowekManager(this);
        this.schowekGUI = new SchowekGUI(this);

        SchowekCommand cmd = new SchowekCommand(this);
        if (getCommand("schowek") != null) {
            getCommand("schowek").setExecutor(cmd);
            getCommand("schowek").setTabCompleter(cmd);
        }

        getServer().getPluginManager().registerEvents(new SchowekListener(this), this);
        getServer().getPluginManager().registerEvents(schowekGUI, this);

        this.dataManager.startAutoSaveTask();

        getLogger().info("Plugin localhost-schowek zostal pomyslnie wlaczony!");
    }

    @Override
    public void onDisable() {
        if (this.dataManager != null) {
            this.dataManager.stopAutoSaveTask();
        }

        getLogger().info("Plugin localhost-schowek zostal pomyslnie wylaczony!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public SchowekManager getSchowekManager() {
        return schowekManager;
    }

    public SchowekGUI getSchowekGUI() {
        return schowekGUI;
    }

    public String color(String text) {
        return configManager.color(text);
    }
}
