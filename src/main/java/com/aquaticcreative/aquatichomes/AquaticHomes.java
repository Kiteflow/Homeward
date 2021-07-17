package com.aquaticcreative.aquatichomes;

import com.aquaticcreative.aquatichomes.commands.*;
import com.aquaticcreative.aquatichomes.managers.DatabaseManager;
import com.aquaticcreative.aquatichomes.utils.Formatting;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.Format;

public final class AquaticHomes extends JavaPlugin {

    public static Plugin plugin;
    public static FileConfiguration config;

    public static int getMaxHomes(Player p){
        if(p.hasPermission("aquatichomes.admin")) return 0;
        for(int i = 1; i < config.getInt("maxhomes"); i++){
            String permission = String.format("aquatichomes.amount.%s", i);

            if(p.hasPermission(permission)) return i;
        }
        return config.getInt("defaulthomes");
    }

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

        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        DatabaseManager.setup();
        Formatting.setStrings();

        registerCommands();

        System.out.println("[AquaticHomes] AquaticHomes enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("[AquaticHomes] AquaticHomes disabled!");
    }
}
