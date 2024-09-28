package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import com.artillexstudios.axplayerwarps.user.Users;
import com.artillexstudios.axplayerwarps.user.WarpUser;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.bukkit.entity.Player;

import java.util.NoSuchElementException;

public class ActionBack extends Action {

    public ActionBack() {
        super("back");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        WarpUser user = Users.get(player);
        CircularFifoQueue<GuiFrame> last = user.getLastGuis();

        GuiFrame lastEl = last.get(last.size() - 1);
        if (lastEl == null) return;
//        System.out.println("remove: " + (last.size() - 1) + " " + lastEl);
        last.remove(lastEl);

        GuiFrame secondLastEl;
        try {
            secondLastEl = last.get(last.size() - 1);
        } catch (NoSuchElementException ex) {
            player.closeInventory();
            return;
        }
//        System.out.println("get: " + (last.size() - 1) + " " + secondLastEl);
        secondLastEl.open();
    }
}
