package dev.kiteflow.homeward.commands;

import dev.kiteflow.homeward.managers.DatabaseManager;
import dev.kiteflow.homeward.utils.Formatting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

            ArrayList<String> homes = DatabaseManager.getPlayerHomes(player);
            Component homesList = Component.text("");

            //noinspection ConstantConditions
            for (int i = 0; i < homes.size(); i++) {
                if(i == 0) homesList = homesList.append(Component.text(homes.get(i), NamedTextColor.WHITE));
                else homesList = homesList.append(Component.text(", " + homes.get(i), NamedTextColor.WHITE));
            }

            if(homes.size() == 0) homesList = Formatting.noHomesSet;

            Component homesMessage = Component.text("").append(Formatting.homesListTitle).append(homesList);

            player.sendMessage(homesMessage);
        }else System.out.println("You must be a player to do this!");

        return true;
    }
}
