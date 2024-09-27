package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import com.artillexstudios.axplayerwarps.user.Users;
import com.artillexstudios.axplayerwarps.user.WarpUser;
import org.bukkit.entity.Player;

public class ActionBack extends Action {

    public ActionBack() {
        super("back");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        WarpUser user = Users.get(player);
        var last = user.getLastGuis();

        var lastEl = last.get(last.size() - 1);
        if (lastEl == null) return;
//        System.out.println("remove: " + (last.size() - 1) + " " + lastEl);
        last.remove(lastEl);

        var secondLastEl = last.get(last.size() - 1);
        if (secondLastEl == null) return;
//        System.out.println("get: " + (last.size() - 1) + " " + secondLastEl);
        secondLastEl.open();
    }
}
