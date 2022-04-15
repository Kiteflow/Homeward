package dev.kiteflow.homeward.utils.storage;

import com.mysql.cj.jdbc.MysqlDataSource;
import dev.kiteflow.homeward.Homeward;
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

            mysqlDataSource.setServerName(Homeward.config.getString("database.host"));
            mysqlDataSource.setPort(Homeward.config.getInt("database.port"));
            mysqlDataSource.setUser(Homeward.config.getString("database.username"));
            mysqlDataSource.setPassword(Homeward.config.getString("database.password"));
            mysqlDataSource.setDatabaseName(Homeward.config.getString("database.databaseName"));
        }

        this.setupDatabase();
    }

    public Connection getConnection() throws SQLException {
        Connection connection = null;

        switch(storageType) {
            case MYSQL -> connection = mysqlDataSource.getConnection();
            case SQLITE -> connection = DriverManager.getConnection("jdbc:sqlite:" + Homeward.plugin.getDataFolder() + "/homes.db");
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
                            "name varchar(32) NOT NULL," +
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
        }
    }
}
