package com.aquaticcreative.aquatichomes.commands;

import com.aquaticcreative.aquatichomes.managers.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class SetHomeCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1){
            return DatabaseManager.getHomes(args[0]);
        }else return null;
    }
}
