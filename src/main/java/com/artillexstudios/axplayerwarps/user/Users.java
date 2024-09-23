package com.artillexstudios.axplayerwarps.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

public class Users {
    private static final WeakHashMap<Player, WarpUser> players = new WeakHashMap<>();

    public static WeakHashMap<Player, WarpUser> getPlayers() {
        return players;
    }

    @NotNull
    public static WarpUser get(Player player) {
        return players.getOrDefault(player, create(player));
    }

    @NotNull
    public static WarpUser create(Player player) {
        WarpUser user = new WarpUser(player);
        players.put(player, user);
        return user;
    }
}
