package dev.kiteflow.homeward.utils.storage;

import dev.kiteflow.homeward.Homeward;
import dev.kiteflow.homeward.utils.homes.Home;
import dev.kiteflow.homeward.utils.homes.HomeLocation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static dev.kiteflow.homeward.Homeward.config;

public class DatabaseQuerier {
    private final DatabaseManager databaseManager = new DatabaseManager(config.getBoolean("storage.mysql.enabled") ? StorageType.MYSQL : StorageType.SQLITE);

    public @NotNull Home getHome(@NotNull String name) throws IllegalArgumentException {
        try {
            Home home = Homeward.homesCache.getCachedHome(name.toLowerCase());

            if(home == null) {
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM homes WHERE LOWER(name) = LOWER(?)");
                statement.setString(1, name);

                ResultSet results = statement.executeQuery();
                if(results.next()) {
                    home = new Home(results.getString("name"), UUID.fromString(results.getString("owner")), new HomeLocation(results.getString("location")), results.getBoolean("public"), results.getInt("visits"));

                    statement.close();
                    connection.close();

                    return home;
                }
            } else return home;
        } catch(SQLException e) {
            Homeward.logger.severe("Error retrieving home!");
            e.printStackTrace();
        }

        throw new IllegalArgumentException(Homeward.formatter.getMessage("homeNotFound"));
    }

    public void setHome(@NotNull Home home) throws IllegalArgumentException {
        try {
            ConfigurationSection homeSettings = config.getConfigurationSection("homes");

            if(homeSettings.getStringList("disabled-worlds").contains(home.getLocation(null).getWorld().getName())
                    && !Bukkit.getPlayer(home.getOwner()).hasPermission("homeward.admin"))
                throw new IllegalArgumentException(Homeward.formatter.getMessage("cannotCreateHome"));

            homeNameCheck(home.getName());
            maxHomesCheck(home.getOwner());

            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO homes VALUES(?, ?, ?, ?, ?)");
            statement.setString(1, home.getName());
            statement.setString(2, home.getOwner().toString());
            statement.setString(3, home.getLocationString());
            statement.setBoolean(4, home.getPublic());
            statement.setInt(5, home.getVisits());


            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error creating home!");
            e.printStackTrace();
        }
    }

    public void deleteHome(@NotNull Home home) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM homes WHERE name = ?");
            statement.setString(1, home.getName());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error deleting home!");
            e.printStackTrace();
        }
    }

    public void renameHome(@NotNull Home home, @NotNull String name) throws IllegalArgumentException {
        try {
            homeNameCheck(name);

            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE homes SET name = ? WHERE name = ?");
            statement.setString(1, name);
            statement.setString(2, home.getName());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error renaming home!");
            e.printStackTrace();
        }
    }

    public void updateVisits(@NotNull Home home) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE homes SET visits = ? WHERE name = ?");
            statement.setInt(1, home.getVisits());
            statement.setString(2, home.getName());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error updating home visits!");
            e.printStackTrace();
        }
    }

    public void updatePrivacy(@NotNull Home home) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE homes SET public = ? WHERE name = ?");
            statement.setBoolean(1, home.getPublic());
            statement.setString(2, home.getName());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error updating home visits!");
            e.printStackTrace();
        }
    }

    public @NotNull ArrayList<String> getPublicPlayerHomes(@NotNull UUID player) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM homes WHERE owner = ? AND public = 1");
            statement.setString(1, player.toString());

            ArrayList<String> homes = new ArrayList<>();

            ResultSet results = statement.executeQuery();
            while(results.next()) homes.add(results.getString("name"));

            statement.close();
            connection.close();

            return homes;
        } catch(SQLException e) {
            Homeward.logger.severe("Error retrieving player homes!");
            e.printStackTrace();
        }

        throw new IllegalArgumentException();
    }

    public @NotNull ArrayList<String> getPrivatePlayerHomes(@NotNull UUID player) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM homes WHERE owner = ?");
            statement.setString(1, player.toString());

            ArrayList<String> homes = new ArrayList<>();

            ResultSet results = statement.executeQuery();
            while(results.next()) homes.add(results.getString("name"));

            statement.close();
            connection.close();

            return homes;
        } catch(SQLException e) {
            Homeward.logger.severe("Error retrieving player homes!");
            e.printStackTrace();
        }

        throw new IllegalArgumentException();
    }

    public ArrayList<String> homeSearch(@NotNull String search) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement;

            ArrayList<String> homes = new ArrayList<>();

            if(search.equals("")) {
                statement = connection.prepareStatement("SELECT name FROM homes WHERE public = 1 ORDER BY visits");
            } else {
                statement = connection.prepareStatement("SELECT name FROM homes WHERE name LIKE ? AND public = 1");
                statement.setString(1, "%" + search + "%");
            }

            ResultSet results = statement.executeQuery();
            while(results.next()) homes.add(results.getString("name"));

            statement.close();
            connection.close();

            return homes;
        } catch(SQLException e) {
            Homeward.logger.severe("Error whilst retrieving home names!");
            e.printStackTrace();
        }

        throw new IllegalArgumentException();
    }


    private void homeNameCheck(String name) throws IllegalArgumentException {
        ConfigurationSection nameSettings = config.getConfigurationSection("names");

        if(!nameSettings.getBoolean("unicode") && name.matches("[^\\u0000-\\u007F]+"))
            throw new IllegalArgumentException(Homeward.formatter.getMessage("unicodeCharacters"));

        if(name.length() > nameSettings.getInt("maxlength") || name.length() > 64)
            throw new IllegalArgumentException(Homeward.formatter.getMessage("homeNameLength"));

        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM homes WHERE LOWER(name) = LOWER(?)");
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();
            if(results.next()) throw new IllegalArgumentException(Homeward.formatter.getMessage("homeAlreadyExists"));

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error whilst checking home name!");
            e.printStackTrace();
        }
    }

    private void maxHomesCheck(UUID player) throws IllegalArgumentException {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM homes WHERE owner = ?");
            statement.setString(1, player.toString());

            ResultSet results = statement.executeQuery();
            int homes = 0;
            while(results.next()) homes++;

            if(homes == Home.playerMaxHomes(player))
                throw new IllegalArgumentException(Homeward.formatter.getMessage("homeLimit"));

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error whilst checking max homes!");
            e.printStackTrace();
        }
    }
}
