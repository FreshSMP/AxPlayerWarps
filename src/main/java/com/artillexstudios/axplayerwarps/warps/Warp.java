package com.artillexstudios.axplayerwarps.warps;

import com.artillexstudios.axplayerwarps.category.Category;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Warp {
    private final int id;
    private UUID owner;
    private Location location;
    private String name;
    private String description;
    private @Nullable Category category;
    private final long created;

    public Warp(int id, long created, String description, String name, Location location, @Nullable Category category, UUID owner) {
        this.id = id;
        this.created = created;
        this.description = description;
        this.name = name;
        this.location = location;
        this.category = category;
        this.owner = owner;
    }

    public void reload() {
        // reload category & other stuff
    }
}
