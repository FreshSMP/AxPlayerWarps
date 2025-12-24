package com.artillexstudios.axplayerwarps.utils;

import com.artillexstudios.axplayerwarps.hooks.currency.CurrencyHook;
import com.artillexstudios.axplayerwarps.placeholders.WarpPlaceholders;

public class FormatUtils {

    public static String formatCurrency(CurrencyHook currencyHook, double amount) {
        return currencyHook == null ? WarpPlaceholders.format(amount) : currencyHook.getDisplayName()
                        .replace("%price%", WarpPlaceholders.format(amount));
    }
}
