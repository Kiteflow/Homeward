package dev.kiteflow.homeward;

import dev.kiteflow.homeward.utils.homes.HomesCache;
import dev.kiteflow.homeward.utils.storage.DatabaseQuerier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Homeward extends JavaPlugin {
    public static Plugin plugin;
    public static FileConfiguration config;
    public static Logger logger;

    public static DatabaseQuerier databaseQuerier;
    public static HomesCache homesCache;

    public static void setupConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();

        config = plugin.getConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = plugin.getLogger();
        setupConfig();

        databaseQuerier = new DatabaseQuerier();

        homesCache = new HomesCache(50);

        try {
            databaseQuerier.getHome("test");
        } catch(IllegalArgumentException e) {
            logger.severe(e.getMessage());
        }

        logger.info("Homeward enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Homeward disabled!");
    }
}
