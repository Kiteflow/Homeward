package dev.kiteflow.homeward.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import static dev.kiteflow.homeward.Homeward.config;

@SuppressWarnings("ConstantConditions")
public class Formatting {

    public static Component prefix = config.getBoolean("prefixEnabled") ?
            LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("prefix").trim() + " ") : null;

    public static Component homeCreated = createString("homeCreated");
    public static Component teleportedToHome = createString("teleportedToHome");

    public static Component homesListTitle = createString("homesListTitle");

    public static Component invalidHomeName = createString("invalidHomeName");
    public static Component homeLimitReached = createString("homeLimitReached");
    public static Component homeNotFound = createString("homeNotFound");
    public static Component dontOwnThisHome = createString("dontOwnThisHome");
    public static Component homeDeleted = createString("homeDeleted");
    public static Component noHomesFound = createString("noHomesFound");
    public static Component cannotSetInWorld = createString("cannotSetInWorld");
    public static Component playerNotFound = createString("playerNotFound");
    public static Component invalidFormat = createString("invalidFormat");

    private static Component createString(String stringName){
        if(prefix != null){
            return Component.text("")
                    .append(prefix).append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString(stringName)));
        }else{
            return Component.text("").append(LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString(stringName)));
        }
    }
}
