package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.utils.homes.Home;
import dev.kiteflow.homeward.utils.homes.HomeLocation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(args.length == 1) {
                try {
                    Home home = new Home(args[0], player.getUniqueId(), new HomeLocation(player.getLocation()), true, 0);
                    home.setHome();

                    player.sendMessage(String.format("Home %s created!", home.getName()));
                } catch(IllegalArgumentException error) {
                    player.sendMessage(error.getMessage());
                }
            }
        } else {
            sender.sendMessage("Cannot run command from console!");
        }

        return true;
    }
}
