package com.artillexstudios.axplayerwarps.commands.subcommands;

import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.exception.CommandErrorException;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public enum Open {
    INSTANCE;

    public void execute(CommandSender sender, @Nullable Player player) {
        Player openTo;
        if (player != null) {
            openTo = player;
        } else {
            if (sender instanceof Player pl) {
                openTo = pl;
            } else {
                throw new CommandErrorException("must-be-player");
            }
        }

        new ActionMenu().run(openTo, null, CONFIG.getString("default-gui", "categories"));
    }
}