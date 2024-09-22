package com.artillexstudios.axplayerwarps.hooks.currency;

import me.aglerr.mobcoins.api.MobCoinsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CURRENCIES;

public class TheOnlyMobCoins implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "TheOnly-MobCoins";
    }

    @Override
    public String getDisplayName() {
        return CURRENCIES.getString("currencies.TheOnly-MobCoins.name");
    }

    @Override
    public boolean worksOffline() {
        return false;
    }

    @Override
    public boolean usesDouble() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        if (MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)) == null) return 0;
        return MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)).getCoins();
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        if (MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)) == null) return;
        MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)).addCoins(amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        if (MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)) == null) return;
        MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)).reduceCoins(amount);
    }
}