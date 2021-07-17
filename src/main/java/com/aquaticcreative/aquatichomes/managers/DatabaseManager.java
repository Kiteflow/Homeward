package com.aquaticcreative.aquatichomes.managers;

import com.aquaticcreative.aquatichomes.AquaticHomes;
import com.aquaticcreative.aquatichomes.utils.Formatting;
import com.mysql.cj.jdbc.MysqlDataSource;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.aquaticcreative.aquatichomes.AquaticHomes.plugin;

public class DatabaseManager {
    private static Connection connection;

    public static void setup(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(AquaticHomes.config.getString("host"));
            dataSource.setPort(AquaticHomes.config.getInt("port"));
            dataSource.setUser(AquaticHomes.config.getString("username"));
            dataSource.setPassword(AquaticHomes.config.getString("password"));
            dataSource.setDatabaseName(AquaticHomes.config.getString("database"));

            try {
                connection = dataSource.getConnection();

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

                System.out.println("[AquaticHomes] Connected to Database!");
            } catch (SQLException e) {
                System.out.println("[AquaticHomes] Please check your database credentials!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        });
    }

    public static void createHome(Player player, String name, Location location){
        try {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE LOWER(name) = LOWER('%s')", name));
            if(rs.next()){
                player.sendMessage(Formatting.homeNameInUse);
                return;
            }

            int maxHomes = AquaticHomes.getMaxHomes(player);
            rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE owner = '%s'", player.getUniqueId()));
            int homes = 0;
            while(rs.next()) homes++;
            if(homes + 1 > maxHomes && maxHomes != 0){
                player.sendMessage(Formatting.homeLimitReached);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    statement.executeUpdate(String.format("INSERT INTO homes VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
                            name, player.getUniqueId(), location.getWorld().getName(),
                            location.getBlockX(), location.getBlockY(), location.getBlockZ()));

                    player.sendMessage(Formatting.homeCreated);
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
                player.sendMessage(Formatting.teleportedToHome);
            }else{
                player.sendMessage(Formatting.homeNotFound);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void delHome(Player player, String name){
        try {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE LOWER(name) = LOWER('%s')", name));
            if(!rs.next()){
                player.sendMessage(Formatting.homeNotFound);
                return;
            }
            rs = statement.executeQuery(String.format("SELECT name FROM homes WHERE LOWER(name) = LOWER('%s') AND owner = '%s'", name, player.getUniqueId()));
            if(!rs.next() && !player.hasPermission("aquatichomes.admin")){
                player.sendMessage(Formatting.dontOwnThisHome);
                return;
            }

            statement.executeUpdate(String.format("DELETE FROM homes WHERE LOWER(name) = LOWER('%s') AND owner = '%s'", name, player.getUniqueId()));
            player.sendMessage("Home deleted!");
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
