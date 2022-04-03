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

                    Homeward.homesCache.cacheHome(home);
                    statement.close();
                    connection.close();

                    return home;
                }
            } else return home;
        } catch(SQLException e) {
            Homeward.logger.severe("Error retrieving home!");
        }

        throw new IllegalArgumentException("Home not found!");
    }

    public void setHome(@NonNull Home home) throws UnsupportedOperationException {
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

    public void deleteHome(@NonNull Home home) throws IllegalAccessError {
    }
//    public void renameHome(@NonNull Home home, @NonNull String name) throws IllegalArgumentException {}
//    public void updateVisits(@NonNull Home home) {}
//    public ArrayList<Home> getPlayerHomes(@NonNull UUID player) {}
//    public ArrayList<Home> getHomes(@NonNull Integer page) {}
//    public ArrayList<Home> homeSearch(@Nullable String search) {}
}
