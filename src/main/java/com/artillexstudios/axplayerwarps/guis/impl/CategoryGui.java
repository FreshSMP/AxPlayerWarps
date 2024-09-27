package com.artillexstudios.axplayerwarps.guis.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.Placeholder;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.user.Users;
import com.artillexstudios.axplayerwarps.user.WarpUser;
import com.artillexstudios.axplayerwarps.warps.WarpManager;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

import java.io.File;

public class CategoryGui extends GuiFrame {
    private static final Config GUI = new Config(new File(AxPlayerWarps.getInstance().getDataFolder(), "guis/categories.yml"),
            AxPlayerWarps.getInstance().getResource("guis/categories.yml"),
            GeneralSettings.builder().setUseDefaults(false).build(),
            LoaderSettings.builder().build(),
            DumperSettings.DEFAULT,
            UpdaterSettings.builder().build()
    );

    private final Gui gui = Gui
            .gui()
            .disableAllInteractions()
            .title(StringUtils.format(GUI.getString("title", "---")))
            .rows(GUI.getInt("rows", 5))
            .create();

    public CategoryGui(Player player) {
        super(GUI, player);
        WarpUser user = Users.get(player);
        setPlaceholder(new Placeholder((pl, s) -> {
            s = s.replace("%total_warps%", "" + WarpManager.getWarps().size());
            s = s.replace("%favorite_warps%", "" + user.getFavorites().size());
            return s;
        }));
        setGui(gui);
    }

    public static boolean reload() {
        return GUI.reload();
    }

    public void open() {
        gui.update();
        gui.open(player);
    }
}
