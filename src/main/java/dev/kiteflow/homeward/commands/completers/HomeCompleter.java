package dev.kiteflow.homeward.commands.completers;

import dev.kiteflow.homeward.managers.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if(args.length == 1){
            return DatabaseManager.getHomes(args[0]);
        }else return null;
    }
}
