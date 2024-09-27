package com.artillexstudios.axplayerwarps.category;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;

import java.util.LinkedHashMap;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public class CategoryManager {
    private static final LinkedHashMap<String, Category> categories = new LinkedHashMap<>();

    // todo: reload parts of Warp
    public static void reload() {
        categories.clear();

        AxPlayerWarps.getThreadedQueue().submit(() -> {
            for (String raw : CONFIG.getSection("categories").getRoutesAsStrings(false)) {
                Section section = CONFIG.getSection("categories." + raw);

                String name = section.getString("name");
                int id = AxPlayerWarps.getDatabase().getCategoryId(raw);

                Category category = new Category(id, raw, name, section);
                categories.put(raw, category);
            }
        });
    }

    public static LinkedHashMap<String, Category> getCategories() {
        return categories;
    }
}
