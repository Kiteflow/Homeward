package dev.kiteflow.homeward.utils.storage;

import dev.kiteflow.homeward.utils.homes.Home;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    StorageType storageType;

    public DatabaseManager(StorageType storageType) {
        this.storageType = storageType;
    }

    public Connection getConnection() throws SQLException {
        return null;
    }
}
