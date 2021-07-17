package dev.kiteflow.homeward.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import static dev.kiteflow.homeward.Homeward.config;

@SuppressWarnings("ConstantConditions")
public class Formatting {

    public static Component homeCreated;
    public static Component teleportedToHome;

    public static Component homesListTitle;

    public static Component homeNameInUse;
    public static Component homeLimitReached;
    public static Component homeNotFound;
    public static Component dontOwnThisHome;
    public static Component homeDeleted;
    public static Component noHomesSet;
    public static Component invalidFormat;

    public static void setStrings() {
        if(config.getBoolean("prefixEnabled")){
            Component prefix = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("prefix").trim() + " ");

            homeCreated = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeCreated")));
            teleportedToHome = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("teleportedToHome")));

            homesListTitle = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homesListTitle").trim() + " "));

            homeNameInUse = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeNameInUse")));
            homeLimitReached = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeLimitReached")));
            homeNotFound = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeNotFound")));
            dontOwnThisHome = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("dontOwnThisHome")));
            homeDeleted = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeDeleted")));
            noHomesSet = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("noHomesSet"));
            invalidFormat = Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("invalidFormat")));
        }else{
            homeCreated = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeCreated"));
            teleportedToHome = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("teleportedToHome"));

            homesListTitle = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homesListTitle").trim() + " ");

            homeNameInUse = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeNameInUse"));
            homeLimitReached = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeLimitReached"));
            homeNotFound = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeNotFound"));
            dontOwnThisHome = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("dontOwnThisHome"));
            homeDeleted = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("homeDeleted"));
            noHomesSet = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("noHomesSet"));
            invalidFormat = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("invalidFormat"));
        }
    }
}
