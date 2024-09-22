package com.artillexstudios.axplayerwarps.world;

import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;

public class WorldManager {
    private static final HashMap<World, Integer> worlds = new HashMap<>();

    // todo: reload parts of Warp
    public static void reload() {
        worlds.clear();

        for (World world : Bukkit.getWorlds()) {
            worlds.put(world, AxPlayerWarps.getDatabase().getWorldId(world));
        }
    }

    public static HashMap<World, Integer> getWorlds() {
        return worlds;
    }

    public static void onWorldLoad(World world) {
        WorldManager.getWorlds().put(world, AxPlayerWarps.getDatabase().getWorldId(world));
    }

    public static void onWorldUnload(World world) {
        WorldManager.getWorlds().remove(world);
    }
}
