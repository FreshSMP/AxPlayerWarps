package com.artillexstudios.axplayerwarps.database;

import com.artillexstudios.axapi.utils.Pair;
import com.artillexstudios.axplayerwarps.category.Category;
import com.artillexstudios.axplayerwarps.hooks.currency.CurrencyHook;
import com.artillexstudios.axplayerwarps.warps.Warp;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Database {

    String getType();

    void setup();

    int getPlayerId(OfflinePlayer offlinePlayer);

    int getPlayerId(UUID uuid);

    String getPlayerName(UUID uuid);

    int getWorldId(String world);

    int getWorldId(World world);

    UUID getUUIDFromId(int id);

    Pair<UUID, String> getUUIDAndNameFromId(int id);

    World getWorldFromId(int id);

    int getCategoryId(String category);

    Category getCategoryFromId(int id);

    int getCurrencyId(String currency);

    CurrencyHook getCurrencyFromId(int id);

    int getMaterialId(Material material);

    int getMaterialId(String material);

    Material getMaterialFromId(int id);

    int createWarp(OfflinePlayer player, Location location, String warpName);

    void updateWarp(Warp warp);

    void deleteWarp(Warp warp);

    void setRating(Player player, Warp warp, int stars);

    void removeRating(Player player, Warp warp);

    @Nullable
    Integer getRating(Player player, Warp warp);

    Pair<Integer, Float> getRatings(Warp warp);

    void addToFavorites(Player player, Warp warp);

    void removeFromFavorites(Player player, Warp warp);

    void removeAllFavorites(Player player);

    int getFavorites(Warp warp);

    int getFavorites(Player player);

    boolean isFavorite(Player player, Warp warp);

    void addVisit(Player player, Warp warp);

    int getVisits(Warp warp);

    int getUniqueVisits(Warp warp);

    boolean warpExists(String name);

    void loadWarps();

    void disable();
}
