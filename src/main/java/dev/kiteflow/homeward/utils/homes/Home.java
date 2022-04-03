package dev.kiteflow.homeward.utils.homes;

import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.UUID;

import static dev.kiteflow.homeward.Homeward.config;
import static dev.kiteflow.homeward.Homeward.databaseQuerier;

public class Home {
    private final UUID owner;
    private final HomeLocation location;
    private String name;
    private Boolean publicHome;
    private Integer visits;

    /**
     * Class constructor
     *
     * @param name       the name of the home
     * @param owner      the UUID of the homeowner
     * @param location   the Location object of the home
     * @param publicHome whether the home is public or not
     * @param visits     the total amount of visits the home has had
     * @throws IllegalArgumentException thrown when one of the arguments, likely the home name is not valid
     */
    public Home(@NonNull String name, @NonNull UUID owner, @NonNull HomeLocation location, @NonNull Boolean publicHome, @NonNull Integer visits) throws IllegalArgumentException {
        this.name = (config.getBoolean("names.unicode") ? name : name.replaceAll("[^\\u0000-\\u007F]+", "")).substring(0, config.getInt("names.maxlength"));
        this.owner = owner;
        this.location = location;
        this.publicHome = publicHome;
        this.visits = visits;
    }

    /**
     * Constructor used when only the home name is known, designed for when trying to teleport to a home
     *
     * @param name the name of the home being searched for
     * @throws IllegalArgumentException thrown when the home is not found
     */
    public Home(@NonNull String name) throws IllegalArgumentException {
        Home home;

        try {
            home = databaseQuerier.getHome(name);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Home not found!");
        }

        this.owner = home.owner;
        this.name = home.name;
        this.location = home.location;
        this.publicHome = home.publicHome;
        this.visits = home.visits;
    }

    /**
     * Get the requested player's homes
     *
     * @param player the UUID of the player whose homes are being requested
     * @return list of the players homes, or null if there aren't any
     */
    public static ArrayList<Home> getPlayerHomes(@NonNull UUID player) {
        return null;
    }

    /**
     * Get an ArrayList of 10 homes from the database, determined by the page number
     *
     * @param page the homes page number being requested
     * @return an arraylist of 10 homes from the requested page number
     */
    public static ArrayList<Home> getHomes(Integer page) {
        return null;
    }

    /**
     * Get an ArrayList of homes whose names match the search string
     *
     * @param search search string used for matching with home names
     * @return an ArrayList of homes whose names match the search string
     */
    public static ArrayList<Home> homeSearch(@Nullable String search) {
        return null;
    }

    /**
     * Get the UUID of the homeowner
     *
     * @return UUID of the homeowner
     * @see Home
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Get the name of the home
     *
     * @return name of the home
     * @see Home
     */
    public String getName() {
        return name;
    }

    /**
     * Set a new name of the home, which is then updated in the database
     *
     * @param player the UUID of the player attempting to change the home name
     * @param name   the new name of the home
     * @throws IllegalAccessError       thrown when the player doesn't have permission to rename the home
     * @throws IllegalArgumentException thrown when the home name is not usable
     */
    public void setName(@NonNull UUID player, @NonNull String name) throws IllegalAccessError, IllegalArgumentException {
        if(player != owner) throw new IllegalAccessError("Not home owner!");
        this.name = name;
    }

    /**
     * Gets the location string
     *
     * @return the location string for the location of the home
     */
    public String getLocationString() {
        return this.location.getLocationString();
    }

    /**
     * Gets the location of the home
     *
     * @param player the UUID of the player attempting to get the home location
     * @return the location of the home
     * @throws IllegalAccessError thrown when the home is not accessible to the player
     */
    public Location getLocation(@NonNull UUID player) throws IllegalAccessError {
        if(!publicHome && (player != owner)) throw new IllegalAccessError("This home is private!");
        return location.getLocation();
    }

    /**
     * Change whether the home is public or private
     *
     * @param player     the UUID of the player changing the home's publicity
     * @param publicHome the new value for whether home is public (true) or private (false)
     * @throws IllegalAccessError thrown when the player cannot change the home's publicity
     */
    public void setPublic(@NonNull UUID player, @NonNull Boolean publicHome) throws IllegalAccessError {
        if(player != owner) throw new IllegalAccessError("Not home owner!");
        this.publicHome = publicHome;
    }

    /**
     * Returns whether the home is public or not
     *
     * @return a boolean for whether home is public (true) or private (false)
     */
    public Boolean getPublic() {
        return this.publicHome;
    }

    /**
     * Get the total visits to the home
     *
     * @return the total visits to the home
     */
    public Integer getVisits() {
        return visits;
    }

    /**
     * Add a new visit to the total, and update it in the database
     */
    public void newVisit() {
        this.visits++;
    }

    /**
     * Set the home in the database
     *
     * @throws UnsupportedOperationException thrown when the home already exists
     */
    public void setHome() throws UnsupportedOperationException {
        databaseQuerier.setHome(this);
    }

    /**
     * @param executor the CommandExecutor, used so it is console compatible
     * @throws IllegalAccessError thrown when the executor does not have permission to delete the home
     */
    public void deleteHome(@NonNull CommandExecutor executor) throws IllegalAccessError {
        if(executor instanceof Player player) {
            if(player.getUniqueId() == owner || player.hasPermission("homeward.admin")) {
                databaseQuerier.deleteHome(this);
            } else throw new IllegalAccessError("Not home owner!");
        } else databaseQuerier.deleteHome(this);
    }
}
