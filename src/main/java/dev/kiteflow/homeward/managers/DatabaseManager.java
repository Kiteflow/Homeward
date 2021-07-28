package dev.kiteflow.homeward.managers;

import com.mysql.cj.jdbc.MysqlDataSource;
import dev.kiteflow.homeward.Homeward;
import dev.kiteflow.homeward.utils.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import static dev.kiteflow.homeward.Homeward.config;

@SuppressWarnings("ConstantConditions")
public class DatabaseManager {
    private static boolean mySql;
    private static MysqlDataSource dataSource;

    private static Connection getConnection(){
        Connection connection;
        try {
            if(mySql){
                connection = dataSource.getConnection();
            }else{
                connection = DriverManager.getConnection("jdbc:sqlite:" + Homeward.plugin.getDataFolder() + "/homes.db");
            }

            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setup(){
        mySql = config.getBoolean("mysql");
        if(mySql){
            dataSource = new MysqlDataSource();
            dataSource.setServerName(config.getString("host"));
            dataSource.setPort(config.getInt("port"));
            dataSource.setUser(config.getString("username"));
            dataSource.setPassword(config.getString("password"));
            dataSource.setDatabaseName(config.getString("database"));
        }

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS homes (\n" +
                            "name varchar(20) NOT NULL," +
                            "owner varchar(36) NOT NULL," +
                            "world varchar(36) NOT NULL," +
                            "x int(11) NOT NULL," +
                            "y int(11) NOT NULL," +
                            "z int(11) NOT NULL)"
            );

            System.out.println("[Homeward] Connected to Database!");
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createHome(Player player, String name, Location location) {
        try {
            Connection connection = getConnection();

            if (config.getStringList("disabled-worlds").contains(location.getWorld().getName()) && !player.hasPermission("homeward.admin")) {
                Homeward.adventure.player(player).sendMessage(Formatting.cannotSetInWorld);
                connection.close();
                return;
            }

            PreparedStatement nameCheck = connection.prepareStatement("SELECT name FROM homes WHERE LOWER(name) = LOWER(?)");
            nameCheck.setString(1, name);
            ResultSet nameResults = nameCheck.executeQuery();
            if (nameResults.next()) {
                Homeward.adventure.player(player).sendMessage(Formatting.homeNameInUse);
                connection.close();
                return;
            }

            PreparedStatement maxHomesCheck = connection.prepareStatement("SELECT name FROM homes WHERE owner = ?");
            maxHomesCheck.setString(1, player.getUniqueId().toString());
            ResultSet maxHomesResults = maxHomesCheck.executeQuery();

            int homes = 0;
            int maxHomes = Homeward.getMaxHomes(player);
            while (maxHomesResults.next()) {
                homes++;
            }

            if (homes + 1 > maxHomes && maxHomes != 0) {
                Homeward.adventure.player(player).sendMessage(Formatting.homeLimitReached);
                connection.close();
                return;
            }

            PreparedStatement setHome = connection.prepareStatement("INSERT INTO homes VALUES(?, ?, ?, ?, ?, ?)");
            setHome.setString(1, name);
            setHome.setString(2, player.getUniqueId().toString());
            setHome.setString(3, location.getWorld().getUID().toString());
            setHome.setInt(4, location.getBlockX());
            setHome.setInt(5, location.getBlockY());
            setHome.setInt(6, location.getBlockZ());

            setHome.executeUpdate();
            Homeward.adventure.player(player).sendMessage(Formatting.homeCreated);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getHome(Player player, String name){
        try {
            Connection connection = getConnection();
            PreparedStatement getHome = connection.prepareStatement("SELECT world, x, y, z FROM homes WHERE LOWER(name) = LOWER(?)");
            getHome.setString(1, name);

            ResultSet home = getHome.executeQuery();
            if(home.next()){
                Location location = new Location(Bukkit.getWorld(UUID.fromString(home.getString("world"))),
                        home.getInt("x"), home.getInt("y"), home.getInt("z"));
                player.teleport(location);
                Homeward.adventure.player(player).sendMessage(Formatting.teleportedToHome);
            }else{
                Homeward.adventure.player(player).sendMessage(Formatting.homeNotFound);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delHome(Player player, String name){
        try {
            Connection connection = getConnection();

            PreparedStatement nameCheck = connection.prepareStatement("SELECT name FROM homes WHERE LOWER(name) = LOWER(?)");
            nameCheck.setString(1, name);
            ResultSet nameResults = nameCheck.executeQuery();
            if(!nameResults.next()) {
                Homeward.adventure.player(player).sendMessage(Formatting.homeNotFound);
                connection.close();
                return;
            }

            PreparedStatement ownerCheck = connection.prepareStatement("SELECT name FROM homes WHERE LOWER(name) = LOWER(?) AND owner = ?");
            ownerCheck.setString(1, name);
            ownerCheck.setString(2, player.getUniqueId().toString());
            ResultSet ownerResults = ownerCheck.executeQuery();
            if(!ownerResults.next() && !player.hasPermission("homeward.admin")){
                Homeward.adventure.player(player).sendMessage(Formatting.dontOwnThisHome);
                connection.close();
                return;
            }

            PreparedStatement deleteHome = connection.prepareStatement("DELETE FROM homes WHERE LOWER(name) = LOWER(?)");
            deleteHome.setString(1, name);

            deleteHome.execute();
            Homeward.adventure.player(player).sendMessage(Formatting.homeDeleted);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getHomes(String search){
        try {
            Connection connection = getConnection();
            ArrayList<String> homes = new ArrayList<>();

            if(search.equals("")){
                PreparedStatement allHomesSearch = connection.prepareStatement("SELECT name FROM homes WHERE 1 ORDER BY name");
                ResultSet homesResults = allHomesSearch.executeQuery();
                while(homesResults.next()) homes.add(homesResults.getString("name"));
            }else{
                PreparedStatement homesSearch = connection.prepareStatement("SELECT name FROM homes WHERE name LIKE ?");
                homesSearch.setString(1, "%" + search + "%");
                ResultSet homesResults = homesSearch.executeQuery();
                while(homesResults.next()) homes.add(homesResults.getString("name"));
            }

            connection.close();
            return homes;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getPlayerHomes(UUID player){
        try {
            Connection connection = getConnection();
            ArrayList<String> homes = new ArrayList<>();

            PreparedStatement homesSearch = connection.prepareStatement("SELECT name FROM homes WHERE owner = ?");
            homesSearch.setString(1, player.toString());
            ResultSet homesResults = homesSearch.executeQuery();
            while(homesResults.next()) homes.add(homesResults.getString("name"));

            connection.close();
            return homes;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
