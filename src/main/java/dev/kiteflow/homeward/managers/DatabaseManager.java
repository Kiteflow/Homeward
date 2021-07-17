package dev.kiteflow.homeward.managers;

import dev.kiteflow.homeward.Homeward;
import dev.kiteflow.homeward.utils.Formatting;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {
    private static Connection connection;

    public static void setup(){
        Bukkit.getScheduler().runTaskAsynchronously(Homeward.plugin, () -> {
            try {
                if(Homeward.config.getBoolean("mysql")){
                    MysqlDataSource dataSource = new MysqlDataSource();
                    dataSource.setServerName(Homeward.config.getString("host"));
                    dataSource.setPort(Homeward.config.getInt("port"));
                    dataSource.setUser(Homeward.config.getString("username"));
                    dataSource.setPassword(Homeward.config.getString("password"));
                    dataSource.setDatabaseName(Homeward.config.getString("database"));
                    connection = dataSource.getConnection();
                }else{
                    String url = "jdbc:sqlite:" + Homeward.plugin.getDataFolder() + "homes.db";
                    connection = DriverManager.getConnection(url);
                }

                Statement statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS homes (\n" +
                                "name varchar(20) NOT NULL," +
                                "owner varchar(36) NOT NULL," +
                                "world varchar(20) NOT NULL," +
                                "x int(11) NOT NULL," +
                                "y int(11) NOT NULL," +
                                "z int(11) NOT NULL)"
                );

                System.out.println("[Homeward] Connected to Database!");
            } catch (SQLException e) {
                System.out.println("[Homeward] Please check your database credentials!");
                Homeward.plugin.getServer().getPluginManager().disablePlugin(Homeward.plugin);
            }
        });
    }

    public static void createHome(Player player, String name, Location location){
        try {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE LOWER(name) = LOWER('%s')", name));
            if(rs.next()){
                Homeward.adventure.player(player).sendMessage(Formatting.homeNameInUse);
                return;
            }

            int maxHomes = Homeward.getMaxHomes(player);
            rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE owner = '%s'", player.getUniqueId()));
            int homes = 0;
            while(rs.next()) homes++;
            if(homes + 1 > maxHomes && maxHomes != 0){
                Homeward.adventure.player(player).sendMessage(Formatting.homeLimitReached);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(Homeward.plugin, () -> {
                try {
                    //noinspection ConstantConditions
                    statement.executeUpdate(String.format("INSERT INTO homes VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
                            name, player.getUniqueId(), location.getWorld().getName(),
                            location.getBlockX(), location.getBlockY(), location.getBlockZ()));

                    Homeward.adventure.player(player).sendMessage(Formatting.homeCreated);
                } catch (SQLException e) { e.printStackTrace(); }
            });
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void getHome(Player player, String name){
        try {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT world, x, y, z FROM homes WHERE LOWER(name) = LOWER('%s')", name));
            if(rs.next()){
                Location location = new Location(Bukkit.getWorld(rs.getString("world")),
                        rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                player.teleport(location);
                Homeward.adventure.player(player).sendMessage(Formatting.teleportedToHome);
            }else{
                Homeward.adventure.player(player).sendMessage(Formatting.homeNotFound);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void delHome(Player player, String name){
        try {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE LOWER(name) = LOWER('%s')", name));
            if(!rs.next()){
                Homeward.adventure.player(player).sendMessage(Formatting.homeNotFound);
                return;
            }
            rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE LOWER(name) = LOWER('%s') AND owner = '%s'", name, player.getUniqueId()));
            if(!rs.next() && !player.hasPermission("aquatichomes.admin")){
                Homeward.adventure.player(player).sendMessage(Formatting.dontOwnThisHome);
                return;
            }

            statement.executeUpdate(String.format("DELETE FROM homes WHERE LOWER(name) = LOWER('%s') AND owner = '%s'", name, player.getUniqueId()));
            Homeward.adventure.player(player).sendMessage(Formatting.homeDeleted);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static ArrayList<String> getHomes(String search){
        try {
            Statement statement = connection.createStatement();
            ArrayList<String> homes = new ArrayList<>();

            if(search.equals("")){
                ResultSet rs = statement.executeQuery("SELECT name FROM homes WHERE 1 ORDER BY name");
                while(rs.next()) homes.add(rs.getString("name"));
            }else{
                ResultSet rs = statement.executeQuery("SELECT name FROM homes WHERE name LIKE '%" + search + "%'");
                while(rs.next()) homes.add(rs.getString("name"));
            }

            return homes;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getPlayerHomes(Player player){
        try {
            Statement statement = connection.createStatement();

            ArrayList<String> homes = new ArrayList<>();
            ResultSet rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE owner = '%s'", player.getUniqueId()));
            while(rs.next()) homes.add(rs.getString("name"));

            return homes;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
