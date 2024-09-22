package com.artillexstudios.axplayerwarps.utils;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;

public class StarUtils {

    public static String getFormatted(int filled, int max) {
        StringBuilder stars = new StringBuilder();
        int starAm = Math.round(filled);
        for (int i = 0; i < starAm; i++) {
            stars.append(LANG.getString("placeholders.star.bright"));
            max--;
        }
        for (int i = 0; i < max; i++) {
            stars.append(LANG.getString("placeholders.star.dark"));
        }
        return stars.toString();
    }
}
