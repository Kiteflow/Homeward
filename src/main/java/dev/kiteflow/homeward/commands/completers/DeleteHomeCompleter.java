package dev.kiteflow.homeward.commands.completers;

import dev.kiteflow.homeward.managers.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeleteHomeCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if(args.length == 1 && sender instanceof Player){
            return DatabaseManager.getPlayerHomes(((Player) sender).getUniqueId());
        }else return null;
    }
}
