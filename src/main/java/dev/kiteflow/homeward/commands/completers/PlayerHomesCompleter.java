package dev.kiteflow.homeward.commands.completers;

import dev.kiteflow.homeward.utils.homes.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerHomesCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1 && sender instanceof Player player)
            return Home.getPlayerHomes(player.getUniqueId());

        return null;
    }
}
