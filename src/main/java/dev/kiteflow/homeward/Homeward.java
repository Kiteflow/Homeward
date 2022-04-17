package dev.kiteflow.homeward;

import dev.kiteflow.homeward.commands.*;
import dev.kiteflow.homeward.commands.completers.HomeSearchCompleter;
import dev.kiteflow.homeward.commands.completers.PlayerHomesCompleter;
import dev.kiteflow.homeward.utils.Metrics;
import dev.kiteflow.homeward.utils.homes.HomesCache;
import dev.kiteflow.homeward.utils.messages.Formatter;
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
    public static Formatter formatter;

    public static void setupConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        config = plugin.getConfig();
    }

    private void registerCompleters() {
        plugin.getCommand("closehome").setTabCompleter(new PlayerHomesCompleter());
        plugin.getCommand("deletehome").setTabCompleter(new PlayerHomesCompleter());
        plugin.getCommand("home").setTabCompleter(new HomeSearchCompleter());
        plugin.getCommand("openhome").setTabCompleter(new PlayerHomesCompleter());
        plugin.getCommand("renamehome").setTabCompleter(new PlayerHomesCompleter());
    }


    private void registerCommands() {
        plugin.getCommand("closehome").setExecutor(new CloseHomeCommand());
        plugin.getCommand("deletehome").setExecutor(new DeleteHomeCommand());
        plugin.getCommand("home").setExecutor(new HomeCommand());
        plugin.getCommand("homes").setExecutor(new HomesCommand());
        plugin.getCommand("openhome").setExecutor(new OpenHomeCommand());
        plugin.getCommand("renamehome").setExecutor(new RenameHomeCommand());
        plugin.getCommand("sethome").setExecutor(new SetHomeCommand());

        registerCompleters();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = plugin.getLogger();
        setupConfig();

        databaseQuerier = new DatabaseQuerier();
        homesCache = new HomesCache(50);
        formatter = new Formatter();

        Metrics metrics = new Metrics(plugin, 13017);

        registerCommands();

        logger.info("Homeward enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Homeward disabled!");
    }
}
