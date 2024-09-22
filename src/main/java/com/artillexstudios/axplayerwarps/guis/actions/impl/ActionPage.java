package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public class ActionPage extends Action {

    public ActionPage() {
        super("page");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        if (!(gui.getGui() instanceof PaginatedGui pGui)) return;

        if (arguments.equalsIgnoreCase("previous")) {
            pGui.previous();
        } else if (arguments.equalsIgnoreCase("next")) {
            pGui.next();
        } else {
            pGui.setPageNum(Integer.parseInt(arguments));
        }

        gui.updateTitle();
    }
}
