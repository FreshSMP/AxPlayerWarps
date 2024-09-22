package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import com.artillexstudios.axplayerwarps.guis.impl.CategoryGui;
import com.artillexstudios.axplayerwarps.guis.impl.WarpsGui;
import org.bukkit.entity.Player;

public class ActionMenu extends Action {

    public ActionMenu() {
        super("menu");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        if (arguments.equalsIgnoreCase("close")) {
            player.closeInventory();
            return;
        }

        arguments = arguments.replace(".yml", "").replace(".yaml", "");

        switch (arguments) {
            case "categories" -> new CategoryGui(player).open();
            case "warps" -> new WarpsGui(player).open();
        }
    }
}
