package dev.kiteflow.homeward;

import dev.kiteflow.homeward.utils.homes.HomesCache;
import dev.kiteflow.homeward.utils.storage.DatabaseManager;
import dev.kiteflow.homeward.utils.storage.DatabaseQuerier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Homeward extends JavaPlugin {
    public static Plugin plugin;
    public static FileConfiguration config;

    public static DatabaseQuerier databaseQuerier;
    public static HomesCache homesCache;

    public static void setupConfig() {
        plugin.saveDefaultConfig();

        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();

        config = plugin.getConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        setupConfig();

        databaseQuerier = new DatabaseQuerier();

        homesCache = new HomesCache(50);

        plugin.getLogger().info("Homeward enabled!");
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Homeward disabled!");
    }
}
