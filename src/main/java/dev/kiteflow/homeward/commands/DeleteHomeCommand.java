package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.utils.homes.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DeleteHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            try {
                Home home = new Home(args[0]);
                home.deleteHome(sender);

                sender.sendMessage(String.format("Deleted home %s!", home.getName()));
            } catch(IllegalArgumentException | IllegalAccessError error) {
                sender.sendMessage(error.getMessage());
            }
        }

        return true;
    }
}
