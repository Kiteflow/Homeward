package com.aquaticcreative.aquatichomes.commands;

import com.aquaticcreative.aquatichomes.managers.DatabaseManager;
import com.aquaticcreative.aquatichomes.utils.Formatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(args.length == 1){
                DatabaseManager.delHome(player, args[0]);
            }else player.sendMessage(Formatting.invalidFormat);
        }else System.out.println("You must be a player to do this!");

        return true;
    }
}