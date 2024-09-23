package com.artillexstudios.axplayerwarps.user;

import com.artillexstudios.axplayerwarps.category.Category;
import com.artillexstudios.axplayerwarps.category.CategoryManager;
import com.artillexstudios.axplayerwarps.sorting.Sort;
import com.artillexstudios.axplayerwarps.sorting.SortingManager;
import org.bukkit.entity.Player;

public class WarpUser {
    private final Player player;
    private int sortingIdx = 0;
    private int categoryIdx = -1;

    public WarpUser(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void resetSorting() {
        sortingIdx = 0;
    }

    public void changeSorting(int am) {
        sortingIdx += am;
    }

    public Sort getSorting() {
        int a = sortingIdx;
        int b = SortingManager.getEnabledSorting().size();
        return SortingManager.getEnabledSorting().get((a % b + b) % b);
    }

    public void resetCategory() {
        categoryIdx = -1;
    }

    public void changeCategory(int am) {
        categoryIdx += am;
    }

    public Category getCategory() {
        int a = categoryIdx;
        int b = CategoryManager.getCategories().size();
        return CategoryManager.getCategories().values().stream().toList().get((a % b + b) % b);
    }
}
