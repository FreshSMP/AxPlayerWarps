package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionConsoleCommand extends Action {

    public ActionConsoleCommand() {
        super("console");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        String formatted = arguments.replace("%player%", player.getName());
        if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
            formatted = PlaceholderAPI.setPlaceholders(player, formatted);
        }

        final String finalFormatted = formatted;
        Scheduler.get().run(task -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalFormatted);
        });
    }
}
