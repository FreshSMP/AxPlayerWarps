package com.artillexstudios.axplayerwarps.guis.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.Placeholder;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Actions;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.utils.StarUtils;
import com.artillexstudios.axplayerwarps.warps.Warp;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;

public class EditWarpGui extends GuiFrame {
    private static final Config GUI = new Config(new File(AxPlayerWarps.getInstance().getDataFolder(), "guis/edit-warp.yml"),
            AxPlayerWarps.getInstance().getResource("guis/edit-warp.yml"),
            GeneralSettings.builder().setUseDefaults(false).build(),
            LoaderSettings.builder().build(),
            DumperSettings.DEFAULT,
            UpdaterSettings.builder().build()
    );

    private final Gui gui;
    private final Warp warp;

    public EditWarpGui(Player player, Warp warp) {
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

        gui.setPlayerInventoryAction(event -> {
            if (event.getCurrentItem() == null) return;
            warp.setIcon(event.getCurrentItem().getType());
            AxPlayerWarps.getDatabase().updateWarp(warp);
            open();
        });
    }

    public static boolean reload() {
        return GUI.reload();
    }

    public void open() {
        GuiItem guiItem = createItem("name-icon", event -> {
            Actions.run(player, this, file.getStringList("name-icon.actions"));
            if (event.isShiftClick() && event.isRightClick()) {
                warp.setIcon(null);
                AxPlayerWarps.getDatabase().updateWarp(warp);
                open();
                return;
            }
            SignInput sign = new SignInput(player, StringUtils.formatList(LANG.getStringList("rename-sign")).toArray(Component[]::new), (player1, result) -> {
                String res = MiniMessage.builder().build().serialize(result[0]);
                if (res.isBlank()) {
                    // todo: invalid name
                    return;
                }
                if (!warp.setName(res.replace(" ", "_"))) {
                     // todo: warp exists
                } else {
                    AxPlayerWarps.getDatabase().updateWarp(warp);
                    // todo: name updated
                }
                Scheduler.get().run(this::open);
            });
            sign.open();
        }, Map.of());
        ItemStack mt = guiItem.getItemStack();
        if (warp.getIcon() != null) mt.setType(warp.getIcon());
        guiItem.setItemStack(mt);

        createItem("location", event -> {
            Actions.run(player, this, file.getStringList("location.actions"));
            warp.setLocation(player.getLocation());
            AxPlayerWarps.getDatabase().updateWarp(warp);
            open();
        }, Map.of());

        createItem("delete", event -> {
            if (event.isShiftClick() && event.isRightClick()) {
                Actions.run(player, this, file.getStringList("delete.actions"));
                AxPlayerWarps.getDatabase().deleteWarp(warp);
                // todo: deleted
                player.closeInventory();
            }
        }, Map.of());

        createItem("description", event -> {
            Actions.run(player, this, file.getStringList("description.actions"));
            List<String> desc = new ArrayList<>(Arrays.stream(warp.getDescription().split("\n")).toList());
            if (event.isLeftClick()) {
                if (CONFIG.getInt("warp-description.max-lines") >= desc.size()) {
                    // todo: max lines reached
                    open();
                    return;
                }
                SignInput sign = new SignInput(player, StringUtils.formatList(LANG.getStringList("add-line-sign")).toArray(Component[]::new), (player1, result) -> {
                    String res = MiniMessage.builder().build().serialize(result[0]);
                    desc.add(res);
                    warp.setDescription(desc);
                    AxPlayerWarps.getDatabase().updateWarp(warp);
                    Scheduler.get().run(this::open);
                });
                sign.open();
                return;
            } else if (event.isRightClick()) {
                if (event.isShiftClick()) {
                    desc.clear();
                    warp.setDescription(desc);
                    AxPlayerWarps.getDatabase().updateWarp(warp);
                    open();
                    return;
                }
                desc.remove(desc.size() - 1);
                warp.setDescription(desc);
                AxPlayerWarps.getDatabase().updateWarp(warp);
                open();
            }
        }, Map.of());

        gui.update();
        gui.open(player);
    }
}
