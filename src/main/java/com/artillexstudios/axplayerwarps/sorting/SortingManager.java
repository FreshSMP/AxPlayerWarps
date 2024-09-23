package com.artillexstudios.axplayerwarps.sorting;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axplayerwarps.enums.Sorting;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public class SortingManager {
    private static final List<Sort> enabledSorting = new ArrayList<>();

    // todo: default
    public static void reload() {
        enabledSorting.clear();

        for (String raw : CONFIG.getSection("sorting").getRoutesAsStrings(false)) {
            Section section = CONFIG.getSection("sorting." + raw);

            Section forwards = section.getSection("forwards");
            if (forwards.getBoolean("enabled")) {
                Sort sort = new Sort(forwards.getString("name"), Sorting.valueOf(raw.toUpperCase()), false);
                enabledSorting.add(sort);
            }
            Section backwards = section.getSection("backwards");
            if (backwards.getBoolean("enabled")) {
                Sort sort = new Sort(backwards.getString("name"), Sorting.valueOf(raw.toUpperCase()), true);
                enabledSorting.add(sort);
            }
        }
    }

    public static List<Sort> getEnabledSorting() {
        return enabledSorting;
    }
}
