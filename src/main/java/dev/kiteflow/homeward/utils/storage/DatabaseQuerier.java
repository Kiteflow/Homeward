package dev.kiteflow.homeward.utils.storage;

import dev.kiteflow.homeward.Homeward;
import dev.kiteflow.homeward.utils.homes.Home;
import dev.kiteflow.homeward.utils.homes.HomeLocation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseQuerier {
    private final DatabaseManager databaseManager = new DatabaseManager(StorageType.SQLITE);

    public @NotNull Home getHome(@NonNull String name) throws IllegalArgumentException {
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

        throw new IllegalArgumentException("Home not found!");
    }

    public void setHome(@NonNull Home home) throws IllegalArgumentException {
        try {
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
        }
    }

    public void deleteHome(@NonNull Home home) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM homes WHERE name = ?");
            statement.setString(1, home.getName());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error deleting home!");
        }
    }

    public void renameHome(@NonNull Home home, @NonNull String name) throws IllegalArgumentException {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE homes SET name = ? WHERE name = ?");
            statement.setString(1, name);
            statement.setString(2, home.getName());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException e) {
            Homeward.logger.severe("Error renaming home!");
        }
    }

    public void updateVisits(@NonNull Home home) {
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
        }
    }

    public void updatePrivacy(@NonNull Home home) {
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
        }
    }

//    public ArrayList<Home> getPlayerHomes(@NonNull UUID player) {}
//    public ArrayList<Home> getHomes(@NonNull Integer page) {}
//    public ArrayList<Home> homeSearch(@Nullable String search) {}
}
