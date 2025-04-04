package com.artillexstudios.axplayerwarps.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.UUID;

public class CoinsEngineHook implements CurrencyHook {
    private Currency currency = null;
    private final String internal;
    private final String name;

    public CoinsEngineHook(String internal, String name) {
        this.internal = internal;
        this.name = name;
    }

    @Override
    public void setup() {
        currency = CoinsEngineAPI.getCurrency(internal);
        if (currency == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxPlayerWarps] CoinsEngine currency named &#DD0000" + internal + " &#FF0000not found! Change the currency-name or disable the hook to get rid of this warning!"));
        }
    }

    @Override
    public String getName() {
        return "CoinsEngine-" + internal;
    }

    @Override
    public String getDisplayName() {
        return name;
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
        if (currency == null) return 0;
        return CoinsEngineAPI.getBalance(Bukkit.getPlayer(player), currency);
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        CoinsEngineAPI.addBalance(Bukkit.getPlayer(player), currency, amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        CoinsEngineAPI.removeBalance(Bukkit.getPlayer(player), currency, amount);
    }
}