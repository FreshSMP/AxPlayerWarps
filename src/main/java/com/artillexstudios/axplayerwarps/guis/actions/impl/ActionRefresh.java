package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import org.bukkit.entity.Player;

public class ActionRefresh extends Action {

    public ActionRefresh() {
        super("refresh");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        gui.open();
    }
}
