package com.artillexstudios.axplayerwarps.warps;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axplayerwarps.category.Category;
import com.artillexstudios.axplayerwarps.enums.Access;
import com.artillexstudios.axplayerwarps.hooks.currency.CurrencyHook;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public class Warp {
    private final int id;
    private UUID owner;
    private Location location;
    private String name;
    private @Nullable String description;
    private @Nullable Category category;
    private final long created;
    private Access access;
    private @Nullable CurrencyHook currency;
    private double teleportPrice;
    private Material icon;

    public Warp(int id, long created, @Nullable String description, String name,
                Location location, @Nullable Category category,
                UUID owner, Access access, @Nullable CurrencyHook currency,
                double teleportPrice, @Nullable Material icon
    ) {
        this.id = id;
        this.created = created;
        this.description = description;
        this.name = name;
        this.location = location;
        this.category = category;
        this.owner = owner;
        this.access = access;
        this.currency = currency;
        this.teleportPrice = teleportPrice;
        this.icon = icon;
    }

    public void reload() {
        // reload category & other stuff
    }

    public int getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if (description == null) {
            return CONFIG.getString("warp-description.default", "");
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public Category getCategory() {
        return category;
    }

    public void setCategory(@Nullable Category category) {
        this.category = category;
    }

    public long getCreated() {
        return created;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Nullable
    public CurrencyHook getCurrency() {
        return currency;
    }

    public void setCurrency(@Nullable CurrencyHook currency) {
        this.currency = currency;
    }

    public double getTeleportPrice() {
        return teleportPrice;
    }

    public void setTeleportPrice(double teleportPrice) {
        this.teleportPrice = teleportPrice;
    }

    public Material getIcon() {
        if (icon == null) {
            return Material.matchMaterial(CONFIG.getString("default-material", "PLAYER_HEAD"));
        }
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public void teleportPlayer(Player player) {
        // todo: check balance
        // todo: check if safe
        // todo: add visitor
        // todo: send message
        PaperUtils.teleportAsync(player, location);
    }
}
