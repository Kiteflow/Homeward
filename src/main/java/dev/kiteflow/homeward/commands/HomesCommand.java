package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.Homeward;
import dev.kiteflow.homeward.managers.DatabaseManager;
import dev.kiteflow.homeward.utils.Formatting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            OfflinePlayer target;

            if(args.length == 0){
                target = Bukkit.getOfflinePlayer(player.getUniqueId());
            }else if(args.length == 1){
                //noinspection deprecation
                target = Bukkit.getOfflinePlayer(args[0]);

                if(target == null){
                    Homeward.adventure.player(player).sendMessage(Formatting.playerNotFound);
                    return true;
                }
            }else{
                Homeward.adventure.player(player).sendMessage(Formatting.invalidFormat);
                return true;
            }

            ArrayList<String> homes = DatabaseManager.getPlayerHomes(target.getUniqueId());
            Component homesList = Component.text(" ");

            //noinspection ConstantConditions
            for (int i = 0; i < homes.size(); i++) {
                if(i == 0) homesList = homesList.append(Component.text(homes.get(i), NamedTextColor.WHITE));
                else homesList = homesList.append(Component.text(", " + homes.get(i), NamedTextColor.WHITE));
            }

            if(homes.size() == 0) homesList = homesList.append(Component.text("No homes found!", NamedTextColor.RED));

            Component homesMessage = Component.text("").append(Formatting.homesListTitle).append(homesList);

            Homeward.adventure.player(player).sendMessage(homesMessage);
        }else System.out.println("You must be a player to do this!");
        return true;
    }
}
