package com.aquaticcreative.aquatichomes.commands;

import com.aquaticcreative.aquatichomes.managers.DatabaseManager;
import com.aquaticcreative.aquatichomes.utils.Formatting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            ArrayList<String> homes = DatabaseManager.getPlayerHomes(player);
            Component homesList = null;

            for (int i = 0; i < homes.size(); i++) {
                if(i == 0) homesList = Component.text(homes.get(i), NamedTextColor.WHITE);
                else homesList = homesList.append(Component.text(", " + homes.get(i), NamedTextColor.WHITE));
            }

            if(homes.size() == 0) homesList = Formatting.noHomesSet;

            Component homesMessage = Component.text("").append(Formatting.homesListTitle).append(homesList);

            player.sendMessage(homesMessage);
        }else System.out.println("You must be a player to do this!");

        return true;
    }
}
