package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.Homeward;
import dev.kiteflow.homeward.managers.DatabaseManager;
import dev.kiteflow.homeward.utils.Formatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(args.length == 1){
                DatabaseManager.getHome(player, args[0]);
            }else Homeward.adventure.player(player).sendMessage(Formatting.invalidFormat);
        }else System.out.println("You must be a player to do this!");

        return true;
    }
}
