package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.utils.homes.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeVisitsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            try {
                Home home = new Home(args[0]);

                if((sender instanceof Player player && home.getOwner().equals(player.getUniqueId()))
                        || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(String.format("Home %s has %s visits", home.getName(), home.getVisits()));
                } else {
                    throw new IllegalAccessError("Not home owner!");
                }
            } catch(IllegalArgumentException | IllegalAccessError error) {
                sender.sendMessage(error.getMessage());
            }
        }

        return true;
    }
}
