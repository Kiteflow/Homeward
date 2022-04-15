package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.utils.homes.Home;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static dev.kiteflow.homeward.Homeward.homesCache;

public class HomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(args.length == 1) {
                try {
                    Home home = homesCache.getCachedHome(args[0]);
                    if(home == null) home = new Home(args[0]);

                    Location location = home.getLocation(player.getUniqueId());

                    player.teleport(location);

                    player.sendMessage(String.format("Teleported to %s!", home.getName()));
                    home.newVisit();
                } catch(IllegalArgumentException | IllegalAccessError error) {
                    player.sendMessage(error.getMessage());
                }
            }
        } else {
            sender.sendMessage("Cannot run command from console!");
        }

        return true;
    }
}
