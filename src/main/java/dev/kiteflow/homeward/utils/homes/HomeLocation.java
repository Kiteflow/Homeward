package dev.kiteflow.homeward.utils.homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HomeLocation {
    private final Location location;
    private final String locationString;

    /**
     * Class constructor when the location is known
     *
     * @param location the Location object
     */
    public HomeLocation(@NotNull Location location) {
        this.location = location;
        //noinspection ConstantConditions
        this.locationString = String.format("%s:%s:%s:%s", this.location.getWorld().getUID(), this.location.getBlockX() + 0.5, this.location.getBlockY(), this.location.getBlockZ() + 0.5);
    }

    /**
     * Class constructor when the Location string is known
     *
     * @param locationString the Location string
     */
    public HomeLocation(@NotNull String locationString) {
        this.locationString = locationString;

        String[] locationData = locationString.split(":");
        this.location = new Location(Bukkit.getWorld(UUID.fromString(locationData[0])), Double.parseDouble(locationData[1]), Double.parseDouble(locationData[2]), Double.parseDouble(locationData[3]));
    }

    /**
     * Get the Location object
     *
     * @return the Location object
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the Location string
     *
     * @return the Location string
     */
    public String getLocationString() {
        return locationString;
    }
}
