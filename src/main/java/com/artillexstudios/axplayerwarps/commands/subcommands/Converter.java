package com.artillexstudios.axplayerwarps.commands.subcommands;

import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.converters.ConverterBase;
import com.artillexstudios.axplayerwarps.converters.PlayerWarpsConverter;
import com.artillexstudios.axplayerwarps.enums.Converters;
import org.bukkit.command.CommandSender;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.MESSAGEUTILS;

public enum Converter {
    INSTANCE;

    public void execute(CommandSender sender, Converters converters) {
        ConverterBase cv = switch (converters) {
            case PLAYER_WARPS -> new PlayerWarpsConverter();
        };
        MESSAGEUTILS.sendLang(sender, "converting");
        AxPlayerWarps.getThreadedQueue().submit(cv::run);
    }
}