package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.utils.homes.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RenameHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(args.length == 2) {
                if(!(args[0].equalsIgnoreCase(args[1]))) {
                    try {
                        Home home = new Home(args[0]);
                        home.setName(player.getUniqueId(), args[1]);

                        player.sendMessage(String.format("Renamed home to %s!", home.getName()));
                    } catch(IllegalArgumentException | IllegalAccessError error) {
                        player.sendMessage(error.getMessage());
                    }
                } else {
                    player.sendMessage("Invalid home name!");
                }
            }
        } else {
            sender.sendMessage("Cannot run command from console!");
        }

        return true;
    }
}
