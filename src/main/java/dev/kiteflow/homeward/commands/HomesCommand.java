package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.Homeward;
import dev.kiteflow.homeward.utils.homes.Home;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomesCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> homes;
        OfflinePlayer target;

        if(args.length == 0 && sender instanceof Player player) {
            target = Bukkit.getOfflinePlayer(player.getUniqueId());
            homes = Home.getPlayerHomes(player.getUniqueId());
        } else if(args.length == 1) {
            target = Bukkit.getOfflinePlayer(args[0]);
            homes = Home.getPublicPlayerHomes(target.getUniqueId());
        } else {
            sender.sendMessage(Homeward.formatter.getMessage("invalidFormat"));
            return true;
        }


        if(homes.size() == 0) sender.sendMessage(Homeward.formatter.getMessage("noHomesFound"));
        else {
            String message = String.format("%s %s", Homeward.formatter.getMessage("homesListTitle").replace("<player>", target.getName()), homes.get(0));

            for(int i = 1; i < homes.size(); i++) message = String.format("%s, %s", message, homes.get(i));
            sender.sendMessage(message);
        }

        return true;
    }
}
