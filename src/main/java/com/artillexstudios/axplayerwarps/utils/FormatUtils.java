package com.artillexstudios.axplayerwarps.utils;

import com.artillexstudios.axplayerwarps.hooks.currency.CurrencyHook;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;

public class FormatUtils {

    public static String formatCurrency(CurrencyHook currencyHook, double amount) {
        return currencyHook == null ? Placeholders.df.format(amount) : currencyHook.getDisplayName()
                        .replace("%price%", Placeholders.df.format(amount));
    }
}
