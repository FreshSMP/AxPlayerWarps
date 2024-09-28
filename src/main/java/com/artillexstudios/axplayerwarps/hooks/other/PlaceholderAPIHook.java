package com.artillexstudios.axplayerwarps.hooks.other;

import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.user.Users;
import com.artillexstudios.axplayerwarps.user.WarpUser;
import com.artillexstudios.axplayerwarps.warps.Warp;
import com.artillexstudios.axplayerwarps.warps.WarpManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @NotNull
    @Override
    public String getAuthor() {
        return "ArtillexStudios";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "axplayerwarps";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    // %axplayerwarps_player_warps%
    // %axplayerwarps_player_warp_limit%
    // %axplayerwarps_total_warps%
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        final String[] args = params.split("_");

        WarpUser user = player.getPlayer() == null ? null : Users.get(player.getPlayer());
        if (user != null) {
            if (params.equalsIgnoreCase("player_warp_limit")) {
                return "" + user.getWarpLimit();
            }
            if (params.equalsIgnoreCase("player_warps")) {
                long warps = WarpManager.getWarps().stream().filter(warp -> warp.getOwner().equals(user.getPlayer().getUniqueId())).count();
                return "" + warps;
            }
        }

        if (params.equalsIgnoreCase("total_warps")) {
            return "" + WarpManager.getWarps().size();
        }

        Optional<Warp> warp = WarpManager.getWarps().stream().filter(w -> w.getName().equalsIgnoreCase(args[0])).findAny();
        if (warp.isEmpty()) return LANG.getString("placeholders.warp-not-found", "---");

        List<String> parsedArgs = new ArrayList<>(Arrays.asList(args));
        parsedArgs.remove(0);
        String fin = "%" + String.join("_", parsedArgs) + "%";
        return Placeholders.parse(warp.get(), player, fin);
    }
}
