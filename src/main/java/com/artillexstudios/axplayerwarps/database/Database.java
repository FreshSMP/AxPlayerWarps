package com.artillexstudios.axplayerwarps.database;

import com.artillexstudios.axplayerwarps.warps.Warp;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Database {

    String getType();

    void setup();

    int getPlayerId(OfflinePlayer offlinePlayer);

    int getPlayerId(UUID uuid);

    int getWorldId(String world);

    int getWorldId(World world);

    UUID getUUIDFromId(int id);

    World getWorldFromId(int id);

    int getCategoryId(String category);

    int createWarp(Player player, Location location, String warpName);

    void updateWarp(Warp warp);

    void disable();
}
