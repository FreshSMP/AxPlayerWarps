package com.artillexstudios.axplayerwarps.guis.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.enums.AccessList;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Actions;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.warps.Warp;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RecentsGui extends GuiFrame {
    private static final Config GUI = new Config(new File(AxPlayerWarps.getInstance().getDataFolder(), "guis/recents.yml"),
            AxPlayerWarps.getInstance().getResource("guis/recents.yml"),
            GeneralSettings.builder().setUseDefaults(false).build(),
            LoaderSettings.builder().build(),
            DumperSettings.DEFAULT,
            UpdaterSettings.builder().build()
    );

    private final PaginatedGui gui;

    public RecentsGui(Player player) {
        super(GUI, player);
        this.gui = Gui.paginated()
            .disableAllInteractions()
            .title(Component.empty())
            .rows(GUI.getInt("rows", 5))
            .pageSize(GUI.getInt("page-size", 21))
            .create();

        setGui(gui);
    }

    @Override
    public void updateTitle() {
        gui.updateTitle(StringUtils.formatToString(GUI.getString("title", ""), new HashMap<>(Map.of("%page%", "" + gui.getCurrentPageNum(), "%pages%", "" + Math.max(1, gui.getPagesNum())))));
    }

    public static boolean reload() {
        return GUI.reload();
    }

    public void open() {
        createItem("back", event -> {
            Actions.run(player, this, file.getStringList("back.actions"));
        }, Map.of());

        load().thenRun(() -> {
            updateTitle();
            gui.open(player);
        });
    }

    public CompletableFuture<Void> load() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        AxPlayerWarps.getThreadedQueue().submit(() -> {
            gui.clearPageItems();
            for (Warp warp : AxPlayerWarps.getDatabase().getRecentWarps(player)) {
                Material icon = warp.getIcon();
                ItemBuilder builder = new ItemBuilder(new ItemStack(icon));
                builder.setName(Placeholders.parse(warp, player, GUI.getString("warp.name")));

                String[] description = warp.getDescription().split("\n");

                List<String> lore = new ArrayList<>();
                List<String> lore2 = new ArrayList<>(GUI.getStringList("warp.lore"));
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
                builder.setLore(Placeholders.parseList(warp, player, lore));
                if (icon == Material.PLAYER_HEAD) {
                    final Player pl = Bukkit.getPlayer(warp.getOwner());
                    if (pl != null) {
                        var textures = NMSHandlers.getNmsHandler().textures(pl);
                        if (textures != null) builder.setTextureValue(textures.getFirst());
                    }
                }
                gui.addItem(new GuiItem(builder.get(), event -> {
                    if (event.isLeftClick()) {
                        warp.teleportPlayer(player);
                    } else {
                        new RateWarpGui(player, warp).open();
                    }
                }));
            }

            Scheduler.get().run(scheduledTask -> {
                future.complete(null);
            });
        });

        return future;
    }
}
