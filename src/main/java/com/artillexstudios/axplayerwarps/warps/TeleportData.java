package com.artillexstudios.axplayerwarps.warps;

import com.artillexstudios.axplayerwarps.hooks.currency.CurrencyHook;
import org.bukkit.Location;

public record TeleportData(Warp warp, long date, Location location, CurrencyHook currency, double teleportPrice) {
}
