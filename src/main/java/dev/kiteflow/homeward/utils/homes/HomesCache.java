package dev.kiteflow.homeward.utils.homes;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.LinkedHashMap;

public class HomesCache {
    private final Integer maxSize;
    public LinkedHashMap<String, Home> cachedHomes = new LinkedHashMap<>();

    public HomesCache(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public void cacheHome(@NonNull Home home) {
        if(cachedHomes.size() >= maxSize) {
            String firstHome = cachedHomes.keySet().iterator().next();
            cachedHomes.remove(firstHome);
        }

        cachedHomes.put(home.getName().toLowerCase(), home);
    }

    public @Nullable Home getCachedHome(@NonNull String name) {
        return cachedHomes.get(name);
    }
}
