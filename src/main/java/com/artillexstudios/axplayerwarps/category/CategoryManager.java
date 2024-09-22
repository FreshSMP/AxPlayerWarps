package com.artillexstudios.axplayerwarps.category;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;

import java.util.HashMap;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public class CategoryManager {
    private static final HashMap<String, Category> categories = new HashMap<>();

    // todo: reload parts of Warp
    public static void reload() {
        categories.clear();

        for (String raw : CONFIG.getStringList("categories")) {
            Section section = CONFIG.getSection("categories." + raw);

            String name = section.getString("name");
            int id = AxPlayerWarps.getDatabase().getCategoryId(raw);

            Category category = new Category(id, raw, name, section);
            categories.put(raw, category);
        }
    }
}
