package com.artillexstudios.axplayerwarps.sorting;

import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.warps.Warp;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class WarpComparator implements Comparator<Warp> {
    private final Sort sort;
    private final Player player;

    public WarpComparator(Sort sort, Player player) {
        this.sort = sort;
        this.player = player;
    }

    @Override
    public int compare(@NotNull Warp w1, @NotNull Warp w2) {
        var db = AxPlayerWarps.getDatabase();
        switch (sort.sorting()) {
            case ALPHABETICAL -> {
                return w1.getName().compareTo(w2.getName()) * (sort.reverse() ? -1 : 1);
            }
            case VISITS -> {
                return Integer.compare(db.getVisits(w1), db.getVisits(w2)) * (sort.reverse() ? 1 : -1);
            }
            case RATING -> {
                return db.getRatings(w1).getValue().compareTo(db.getRatings(w2).getValue()) * (sort.reverse() ? 1 : -1);
            }
            case RATING_COUNT -> {
                return db.getRatings(w1).getKey().compareTo(db.getRatings(w2).getKey()) * (sort.reverse() ? 1 : -1);
            }
            case FAVORITES -> {
                return Integer.compare(db.getFavorites(w1), db.getFavorites(w2)) * (sort.reverse() ? 1 : -1);
            }
            case DISTANCE -> {
                return Double.compare(distance(w1), distance(w2)) * (sort.reverse() ? -1 : 1);
            }
            case CREATION_DATE -> {
                return Long.compare(w1.getCreated(), w2.getCreated()) * (sort.reverse() ? 1 : -1);
            }
        }

        return 0;
    }

    private double distance(Warp warp) {
        if (!player.getWorld().equals(warp.getLocation().getWorld())) return Double.MAX_VALUE;
        return player.getLocation().distanceSquared(warp.getLocation());
    }
}
