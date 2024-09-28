package com.artillexstudios.axplayerwarps.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.warps.Warp;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;

public enum Info {
    INSTANCE;

    public void execute(CommandSender sender, Warp warp) {
        String[] description = warp.getDescription().split("\n");
        List<String> lore = new ArrayList<>();
        List<String> lore2 = new ArrayList<>(LANG.getStringList("info"));
        for (int i = 0; i < lore2.size(); i++) {
            String line = lore2.get(i);
            if (!line.contains("%description%")) {
                lore.add(line);
                continue;
            }
            for (int j = description.length - 1; j >= 0; j--) {
                lore.add(i, line.replace("%description%", description[j]));
            }
        }

        for (String s : Placeholders.parseList(warp, null, lore)) {
            sender.sendMessage(StringUtils.formatToString(s));
        }
    }
}