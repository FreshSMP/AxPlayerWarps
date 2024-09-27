package com.artillexstudios.axplayerwarps.warps;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.category.Category;
import com.artillexstudios.axplayerwarps.database.impl.Base;
import com.artillexstudios.axplayerwarps.enums.Access;
import com.artillexstudios.axplayerwarps.enums.AccessList;
import com.artillexstudios.axplayerwarps.hooks.currency.CurrencyHook;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public class Warp {
    private final int id;
    private UUID owner;
    private String ownerName;
    private Location location;
    private String name;
    private @Nullable String description;
    private @Nullable Category category;
    private final long created;
    private Access access;
    private @Nullable CurrencyHook currency;
    private double teleportPrice;
    private Material icon;
    private int favorites;
    private HashMap<UUID, Integer> rating = new HashMap<>();
    private int visits;
    private HashSet<UUID> visitors = new HashSet<>();
    private List<Base.AccessPlayer> whitelisted = Collections.synchronizedList(new ArrayList<>());
    private List<Base.AccessPlayer> blacklisted = Collections.synchronizedList(new ArrayList<>());

    public Warp(int id, long created, @Nullable String description, String name,
                Location location, @Nullable Category category,
                UUID owner, String ownerName, Access access, @Nullable CurrencyHook currency,
                double teleportPrice, @Nullable Material icon
    ) {
        this.id = id;
        this.created = created;
        this.description = description;
        this.name = name;
        this.location = location;
        this.category = category;
        this.owner = owner;
        this.ownerName = ownerName;
        this.access = access;
        this.currency = currency;
        this.teleportPrice = teleportPrice;
        this.icon = icon;

        AxPlayerWarps.getThreadedQueue().submit(() -> {
            favorites = AxPlayerWarps.getDatabase().getFavorites(this);
            rating = AxPlayerWarps.getDatabase().getAllRatings(this);
            visits = AxPlayerWarps.getDatabase().getVisits(this);
            visitors = AxPlayerWarps.getDatabase().getVisitors(this);
            whitelisted = AxPlayerWarps.getDatabase().getAccessList(this, AccessList.WHITELIST);
            blacklisted = AxPlayerWarps.getDatabase().getAccessList(this, AccessList.BLACKLIST);
        });
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

    public boolean setName(String name) {
        if (AxPlayerWarps.getDatabase().warpExists(name)) return false;
        this.name = name;
        return true;
    }

    public String getDescription() {
        if (description == null) {
            return CONFIG.getString("warp-description.default", "");
        }
        return description;
    }

    @Nullable
    public String getRealDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public void setDescription(List<String> description) {
        String newDesc = String.join("\n", description);
        this.description = newDesc.isBlank() ? null : newDesc;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public HashMap<UUID, Integer> getAllRatings() {
        return rating;
    }

    public float getRating() {
        return (float) rating.values().stream().mapToDouble(Integer::doubleValue).average().orElse(0);
    }

    public int getRatingAmount() {
        return rating.size();
    }

    public void setRating(HashMap<UUID, Integer> rating) {
        this.rating = rating;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public HashSet<UUID> getVisitors() {
        return visitors;
    }

    public int getUniqueVisits() {
        return visitors.size();
    }

    public List<Base.AccessPlayer> getBlacklisted() {
        return blacklisted;
    }

    public List<Base.AccessPlayer> getWhitelisted() {
        return whitelisted;
    }

    public List<Base.AccessPlayer> getAccessList(AccessList al) {
        return al == AccessList.WHITELIST ? whitelisted : blacklisted;
    }

    public void teleportPlayer(Player player) {
        // todo: check balance
        // todo: check if safe
        // todo: send message
        // todo: check whitelist/blacklist
        // todo: check access state
        // todo: double click if not safe / paid
        PaperUtils.teleportAsync(player, location);
        AxPlayerWarps.getThreadedQueue().submit(() -> AxPlayerWarps.getDatabase().addVisit(player, this));
    }
}
