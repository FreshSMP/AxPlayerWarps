package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axplayerwarps.category.Category;
import com.artillexstudios.axplayerwarps.category.CategoryManager;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import com.artillexstudios.axplayerwarps.guis.impl.WarpsGui;
import org.bukkit.entity.Player;

public class ActionCategory extends Action {

    public ActionCategory() {
        super("category");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        Category category = CategoryManager.getCategories().get(arguments);
        if (category == null) new WarpsGui(player).open();
        else new WarpsGui(player, category).open();
    }
}
