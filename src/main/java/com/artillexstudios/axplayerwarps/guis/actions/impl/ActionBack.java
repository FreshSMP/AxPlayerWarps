package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import com.artillexstudios.axplayerwarps.user.Users;
import org.bukkit.entity.Player;

public class ActionBack extends Action {

    public ActionBack() {
        super("back");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        var last = Users.get(player).getLastGuis();
        last.remove();
        var el = last.poll();
        if (el == null) return;
        el.open();
    }
}
