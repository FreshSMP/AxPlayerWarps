package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import com.artillexstudios.axplayerwarps.utils.SoundUtils;
import org.bukkit.entity.Player;

public class ActionSound extends Action {

    public ActionSound() {
        super("sound");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        Scheduler.get().run(task -> {
            SoundUtils.playSound(player, arguments);
        });
    }
}