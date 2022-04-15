package dev.kiteflow.homeward;

import dev.kiteflow.homeward.commands.*;
import dev.kiteflow.homeward.utils.homes.HomesCache;
import dev.kiteflow.homeward.utils.storage.DatabaseQuerier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Homeward extends JavaPlugin {
    public static JavaPlugin plugin;
    public static FileConfiguration config;
    public static Logger logger;

    public static DatabaseQuerier databaseQuerier;
    public static HomesCache homesCache;

    public static void setupConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        config = plugin.getConfig();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        plugin.getCommand("closehome").setExecutor(new CloseHomeCommand());
        plugin.getCommand("deletehome").setExecutor(new DeleteHomeCommand());
        plugin.getCommand("home").setExecutor(new HomeCommand());
        plugin.getCommand("homevisits").setExecutor(new HomeVisitsCommand());
        plugin.getCommand("openhome").setExecutor(new OpenHomeCommand());
        plugin.getCommand("renamehome").setExecutor(new RenameHomeCommand());
        plugin.getCommand("sethome").setExecutor(new SetHomeCommand());
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = plugin.getLogger();
        setupConfig();

        databaseQuerier = new DatabaseQuerier();
        homesCache = new HomesCache(50);

        registerCommands();

        logger.info("Homeward enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Homeward disabled!");
    }
}
