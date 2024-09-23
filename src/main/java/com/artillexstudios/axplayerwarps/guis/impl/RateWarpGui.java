package com.artillexstudios.axplayerwarps.guis.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.Placeholder;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Actions;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.utils.StarUtils;
import com.artillexstudios.axplayerwarps.warps.Warp;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;

public class RateWarpGui extends GuiFrame {
    private static final Config GUI = new Config(new File(AxPlayerWarps.getInstance().getDataFolder(), "guis/rate-warp.yml"),
            AxPlayerWarps.getInstance().getResource("guis/rate-warp.yml"),
            GeneralSettings.builder().setUseDefaults(false).build(),
            LoaderSettings.builder().build(),
            DumperSettings.DEFAULT,
            UpdaterSettings.builder().build()
    );

    private final Gui gui;
    private final Warp warp;

    public RateWarpGui(Player player, Warp warp) {
        super(GUI, player, new Placeholder((pl, s) -> {
            Integer rating = AxPlayerWarps.getDatabase().getRating(player, warp);
            s = s.replace("%given_rating_decimal%", rating == null ? "" : Placeholders.df.format(rating));
            s = s.replace("%given_rating_stars%", rating == null ? LANG.getString("placeholders.no-rating") : StarUtils.getFormatted(rating, 5));
            return s;
        }));
        this.warp = warp;
        this.gui = Gui.gui()
            .disableAllInteractions()
            .title(StringUtils.format(GUI.getString("title", ""), Map.of("%warp%", warp.getName())))
            .rows(GUI.getInt("rows", 5))
            .create();

        setWarp(warp);
        setGui(gui);
    }

    public static boolean reload() {
        return GUI.reload();
    }

    public void open() {
        boolean isFavorite = AxPlayerWarps.getDatabase().isFavorite(player, warp);
        createItem("favorite." + (isFavorite ? "favorite" : "not-favorite"), event -> {
            Actions.run(player, this, file.getStringList("favorite.actions"));
            AxPlayerWarps.getThreadedQueue().submit(() -> {
                if (isFavorite) {
                    AxPlayerWarps.getDatabase().removeFromFavorites(player, warp);
                } else {
                    AxPlayerWarps.getDatabase().addToFavorites(player, warp);
                }
                Scheduler.get().run(this::open);
            });
        }, Map.of(), getSlots("favorite"));

        createItem("teleport", event -> {
            Actions.run(player, this, file.getStringList("teleport.actions"));
            warp.teleportPlayer(player);
        }, Map.of());

        createItem("rate", event -> {
            Actions.run(player, this, file.getStringList("rate.actions"));
            if (event.isRightClick()) {
                AxPlayerWarps.getThreadedQueue().submit(() -> {
                    AxPlayerWarps.getDatabase().removeRating(player, warp);
                    Scheduler.get().run(this::open);
                });
            }
            if (event.isLeftClick()) {
                SignInput sign = new SignInput(player, StringUtils.formatList(LANG.getStringList("rating-sign")).toArray(Component[]::new), (player1, result) -> {
                    String res = MiniMessage.builder().build().serialize(result[0]);
                    if (!NumberUtils.isInt(res)) {
                        // todo: not a number
                    } else {
                        int i = Math.max(1, Math.min(5, Integer.parseInt(res)));
                        AxPlayerWarps.getThreadedQueue().submit(() -> {
                            AxPlayerWarps.getDatabase().setRating(player, warp, i);
                        });
                    }
                    Scheduler.get().run(this::open);
                });
                sign.open();
            }
        }, Map.of());

        gui.update();
        gui.open(player);
    }
}
