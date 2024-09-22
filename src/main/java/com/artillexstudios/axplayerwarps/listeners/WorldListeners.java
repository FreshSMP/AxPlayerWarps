package com.artillexstudios.axplayerwarps.listeners;

import com.artillexstudios.axplayerwarps.world.WorldManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListeners implements Listener {

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        WorldManager.onWorldLoad(event.getWorld());
    }

    @EventHandler
    public void onUnload(WorldUnloadEvent event) {
        WorldManager.onWorldUnload(event.getWorld());
    }
}
