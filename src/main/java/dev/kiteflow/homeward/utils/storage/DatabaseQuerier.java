package dev.kiteflow.homeward.utils.storage;

import dev.kiteflow.homeward.utils.homes.Home;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class DatabaseQuerier {
    public DatabaseQuerier() {
        DatabaseManager databaseManager = new DatabaseManager(StorageType.SQLITE);
    }

    public Home getHome(@NonNull String name) throws IllegalArgumentException {}
    public void setHome(@NonNull Home home) throws UnsupportedOperationException {}
    public void deleteHome(@NonNull Home home) throws IllegalAccessError {}
    public void renameHome(@NonNull Home home, @NonNull String name) throws IllegalArgumentException {}
    public void updateVisits(@NonNull Home home) {}
    public ArrayList<Home> getPlayerHomes(@NonNull UUID player) {}
    public ArrayList<Home> getHomes(@NonNull Integer page) {}
    public ArrayList<Home> homeSearch(@Nullable String search) {}
}
