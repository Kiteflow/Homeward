package dev.kiteflow.homeward.utils.homes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class HomesCache {
    private final Integer maxSize;
    public LinkedHashMap<String, Home> cachedHomes = new LinkedHashMap<>();

    public HomesCache(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public void cacheHome(@NotNull Home home) {
        if(cachedHomes.size() >= maxSize) {
            String firstHome = cachedHomes.keySet().iterator().next();
            cachedHomes.remove(firstHome);
        }

        cachedHomes.put(home.getName().toLowerCase(), home);
    }

    public @Nullable Home getCachedHome(@NotNull String name) {
        return cachedHomes.get(name);
    }

    public void removeHome(@NotNull Home home) {
        cachedHomes.remove(home.getName());
    }
}
