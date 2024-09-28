package com.artillexstudios.axplayerwarps.commands.subcommands;

import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.enums.Access;
import com.artillexstudios.axplayerwarps.user.Users;
import com.artillexstudios.axplayerwarps.user.WarpUser;
import com.artillexstudios.axplayerwarps.warps.Warp;
import com.artillexstudios.axplayerwarps.warps.WarpManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.MESSAGEUTILS;

public enum Create {
    INSTANCE;

    public void execute(Player sender, String warpName, @Nullable OfflinePlayer setPlayer) {
        WarpUser user = Users.get(sender);
        long limit = user.getWarpLimit();
        long warps = WarpManager.getWarps().stream().filter(warp -> warp.getOwner().equals(sender.getUniqueId())).count();
        if (limit <= warps) {
            MESSAGEUTILS.sendLang(sender, "errors.limit-reached",
                    Map.of("%current%", "" + warps, "%limit%", "" + limit));
            return;
        }

        AxPlayerWarps.getThreadedQueue().submit(() -> {
            if (AxPlayerWarps.getDatabase().warpExists(warpName)) {
                MESSAGEUTILS.sendLang(sender, "errors.name-exists");
                return;
            }
            OfflinePlayer usedPlayer = setPlayer == null ? sender : setPlayer;
            int id = AxPlayerWarps.getDatabase().createWarp(usedPlayer, sender.getLocation(), warpName);
            Warp warp = new Warp(id, System.currentTimeMillis(), null, warpName, sender.getLocation(), null, usedPlayer.getUniqueId(), usedPlayer.getName(), Access.PUBLIC, null, 0, 0, null);
            MESSAGEUTILS.sendLang(sender, "create.created", Map.of("%warp%", warpName));
            WarpManager.getWarps().add(warp);
        });
    }
}