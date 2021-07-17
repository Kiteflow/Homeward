package dev.kiteflow.homeward;

import dev.kiteflow.homeward.commands.*;
import dev.kiteflow.homeward.managers.DatabaseManager;
import dev.kiteflow.homeward.utils.Formatting;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class Homeward extends JavaPlugin {

    public static Plugin plugin;
    public static FileConfiguration config;
    public static BukkitAudiences adventure;

    public static int getMaxHomes(Player p){
        if(p.hasPermission("homeward.admin")) return 0;
        for(int i = 1; i < config.getInt("maxhomes"); i++){
            String permission = String.format("homeward.amount.%s", i);

            if(p.hasPermission(permission)) return i;
        }
        return config.getInt("defaulthomes");
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands(){
        this.getCommand("deletehome").setExecutor(new DeleteHomeCommand());
        this.getCommand("home").setExecutor(new HomeCommand());
        this.getCommand("home").setTabCompleter(new SetHomeCompleter());
        this.getCommand("homes").setExecutor(new HomesCommand());
        this.getCommand("sethome").setExecutor(new SetHomeCommand());
    }

    @Override
    public void onEnable() {
        plugin = this;
        adventure = BukkitAudiences.create(this);

        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        DatabaseManager.setup();
        Formatting.setStrings();

        registerCommands();

        System.out.println("[Homeward] Homeward enabled!");
    }

    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
        System.out.println("[Homeward] AquaticHomes disabled!");
    }
}
