package dev.kiteflow.homeward.utils.storage;

import com.mysql.cj.jdbc.MysqlDataSource;
import dev.kiteflow.homeward.Homeward;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static dev.kiteflow.homeward.utils.storage.StorageType.MYSQL;
import static dev.kiteflow.homeward.utils.storage.StorageType.SQLITE;

public class DatabaseManager {
    StorageType storageType;
    MysqlDataSource mysqlDataSource;

    public DatabaseManager(@NotNull StorageType storageType) {
        this.storageType = storageType;

        if(storageType == MYSQL) {
            mysqlDataSource = new MysqlDataSource();

            ConfigurationSection settings = Homeward.config.getConfigurationSection("storage.mysql");

            mysqlDataSource.setServerName(settings.getString("host"));
            mysqlDataSource.setPort(settings.getInt("port"));
            mysqlDataSource.setUser(settings.getString("username"));
            mysqlDataSource.setPassword(settings.getString("password"));
            mysqlDataSource.setDatabaseName(settings.getString("database"));
        }

        this.setupDatabase();
    }

    public Connection getConnection() throws SQLException {
        Connection connection;
        switch(storageType) {
            case MYSQL -> connection = mysqlDataSource.getConnection();
            default -> connection = DriverManager.getConnection("jdbc:sqlite:" + Homeward.plugin.getDataFolder() + "/homes.db");
        }

        return connection;
    }

    private void setupDatabase() {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            //@formatter:off
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS homes(" +
                            "name varchar(64) NOT NULL," +
                            "owner varchar(36) NOT NULL," +
                            "location varchar(60) NOT NULL," +
                            "public BOOLEAN NOT NULL," +
                            "visits INT UNSIGNED NOT NULL," +
                            "PRIMARY KEY (name)" +
                            ")"
            );
            //@formatter:on

            statement.close();
            connection.close();

            Homeward.logger.info("Connected to database!");
        } catch(SQLException e) {
            Homeward.logger.severe("Cannot create database tables! Reverting to SQLite!");

            this.storageType = SQLITE;
            setupDatabase();
        }
    }
}
