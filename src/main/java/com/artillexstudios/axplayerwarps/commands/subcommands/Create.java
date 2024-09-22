package com.artillexstudios.axplayerwarps.commands.subcommands;

import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.enums.Access;
import com.artillexstudios.axplayerwarps.warps.Warp;
import com.artillexstudios.axplayerwarps.warps.WarpManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public enum Create {
    INSTANCE;

    public void execute(Player sender, String warpName, @Nullable OfflinePlayer setPlayer) {
        OfflinePlayer usedPlayer = setPlayer == null ? sender : setPlayer;
        AxPlayerWarps.getThreadedQueue().submit(() -> {
            int id = AxPlayerWarps.getDatabase().createWarp(usedPlayer, sender.getLocation(), warpName);
            Warp warp = new Warp(id, System.currentTimeMillis(), null, warpName, sender.getLocation(), null, usedPlayer.getUniqueId(), Access.PUBLIC, null, 0, null);
            WarpManager.getWarps().add(warp);
        });
    }
}