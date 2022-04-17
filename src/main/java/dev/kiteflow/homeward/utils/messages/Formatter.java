package dev.kiteflow.homeward.utils.messages;

import dev.kiteflow.homeward.Homeward;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class Formatter {
    private final HashMap<String, String> messages = new HashMap<>();
    private final ConfigurationSection configMessages;
    private String prefix;

    public Formatter() {
        configMessages = Homeward.config.getConfigurationSection("messages");
        prefix = configMessages.getString("prefix");

        configMessages.getKeys(false).forEach(key -> messages.put(key, configMessages.getString(key)));

        if(!prefix.equals("")) prefix = prefix + " ";
    }

    public String getMessage(String name) {
        return ChatColor.translateAlternateColorCodes('&', prefix + messages.get(name));
    }
}
