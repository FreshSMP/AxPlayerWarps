package com.artillexstudios.axplayerwarps;

import java.util.List;
import java.util.Map;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.INPUT;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;

public class InputConverter {
    private static final Map<String, String> mapping = Map.of(
            "rating-sign", "rate",
            "search-sign", "search",
            "rename-sign", "rename",
            "price-sign", "price",
            "add-line-sign", "add-line",
            "add-player-sign", "add-player"
    );

    public static void start() {
        int done = 0;
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            List<String> list = LANG.getStringList(entry.getKey());
            if (list.isEmpty()) continue;
            INPUT.set(entry.getValue() + ".sign", list);
            LANG.getBackingDocument().remove(entry.getKey());
            done++;
        }
        if (done == 0) return;
        INPUT.save();
        LANG.save();
    }
}
