package dev.kiteflow.homeward.utils.homes;

import dev.kiteflow.homeward.Homeward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

import static dev.kiteflow.homeward.Homeward.databaseQuerier;
import static dev.kiteflow.homeward.Homeward.homesCache;

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
    public Home(@NotNull String name, @NotNull UUID owner, @NotNull HomeLocation location, @NotNull Boolean publicHome, @NotNull Integer visits) throws IllegalArgumentException {
        this.name = name;
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
    public Home(@NotNull String name) throws IllegalArgumentException {
        Home home;

        try {
            home = databaseQuerier.getHome(name);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(Homeward.formatter.getMessage("homeNotFound"));
        }

        this.owner = home.owner;
        this.name = home.name;
        this.location = home.location;
        this.publicHome = home.publicHome;
        this.visits = home.visits;
    }

    /**
     * Get the requested player's public homes
     *
     * @param player the UUID of the player whose homes are being requested
     * @return list of the player's public homes
     */
    public static @NotNull ArrayList<String> getPublicPlayerHomes(@NotNull UUID player) {
        return databaseQuerier.getPublicPlayerHomes(player);
    }

    /**
     * Get the requested player's homes
     *
     * @param player the UUID of the player whose homes are being requested
     * @return list of the player's homes
     */
    public static @NotNull ArrayList<String> getPlayerHomes(@NotNull UUID player) {
        return databaseQuerier.getPrivatePlayerHomes(player);
    }

    /**
     * Get an ArrayList of homes whose names match the search string
     *
     * @param search search string used for matching with home names
     * @return an ArrayList of home names that match the search string
     */
    public static ArrayList<String> homeSearch(@NotNull String search) {
        return databaseQuerier.homeSearch(search);
    }


    /**
     * Get the maximum amount of homes a specified player can have.
     * Do not call if Player is not online!
     *
     * @param uuid the UUID of the player
     * @return the maximum amount of homes this player can have
     */
    public static int playerMaxHomes(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if(player.hasPermission("homeward.admin")) return Integer.MAX_VALUE;

        for(int i = 1; i < 64; i++) {
            String permission = String.format("homeward.homes.%s", i);

            if(player.hasPermission(permission)) return i;
        }

        return Homeward.config.getInt("homes.defaulthomes");
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
    public void setName(@NotNull UUID player, @NotNull String name) throws IllegalAccessError, IllegalArgumentException {
        if(!owner.equals(player)) throw new IllegalAccessError("Not home owner!");
        databaseQuerier.renameHome(this, name);
        homesCache.removeHome(this);

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
    public Location getLocation(@Nullable UUID player) throws IllegalAccessError {
        if(player == null) return location.getLocation();

        if((!publicHome && !player.equals(owner)))
            throw new IllegalAccessError(Homeward.formatter.getMessage("homePrivate"));

        this.newVisit();
        return location.getLocation();
    }

    /**
     * Change whether the home is public or private
     *
     * @param player     the UUID of the player changing the home's publicity
     * @param publicHome the new value for whether home is public (true) or private (false)
     * @throws IllegalAccessError thrown when the player cannot change the home's publicity
     */
    public void setPublic(@NotNull UUID player, @NotNull Boolean publicHome) throws IllegalAccessError {
        if(!owner.equals(player)) throw new IllegalAccessError(Homeward.formatter.getMessage("notHomeOwner"));

        this.publicHome = publicHome;
        databaseQuerier.updatePrivacy(this);

        homesCache.removeHome(this);
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
        databaseQuerier.updateVisits(this);
        homesCache.cacheHome(this);
    }

    /**
     * Set the home in the database
     *
     * @throws IllegalArgumentException thrown when the home already exists
     */
    public void setHome() throws IllegalArgumentException {
        databaseQuerier.setHome(this);
    }

    /**
     * @param sender the CommandSender, used so it is console compatible
     * @throws IllegalAccessError thrown when the executor does not have permission to delete the home
     */
    public void deleteHome(@NotNull CommandSender sender) throws IllegalAccessError {
        if(sender instanceof Player player) {
            if(player.getUniqueId().equals(owner) || player.hasPermission("homeward.admin")) {
                databaseQuerier.deleteHome(this);

                homesCache.removeHome(this);
            } else throw new IllegalAccessError(Homeward.formatter.getMessage("notHomeOwner"));
        } else databaseQuerier.deleteHome(this);

        homesCache.removeHome(this);
    }
}
