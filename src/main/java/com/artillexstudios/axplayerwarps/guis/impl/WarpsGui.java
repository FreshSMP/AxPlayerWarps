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
import com.artillexstudios.axplayerwarps.category.Category;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.warps.Warp;
import com.artillexstudios.axplayerwarps.warps.WarpManager;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class WarpsGui extends GuiFrame {
    private static final Config GUI = new Config(new File(AxPlayerWarps.getInstance().getDataFolder(), "guis/warps.yml"),
            AxPlayerWarps.getInstance().getResource("guis/warps.yml"),
            GeneralSettings.builder().setUseDefaults(false).build(),
            LoaderSettings.builder().build(),
            DumperSettings.DEFAULT,
            UpdaterSettings.builder().build()
    );

    private final PaginatedGui gui = Gui
            .paginated()
            .disableAllInteractions()
            .title(Component.empty())
            .rows(GUI.getInt("rows", 5))
            .pageSize(GUI.getInt("page-size", 27))
            .create();

    private Category category = null;
    private String search = null;

    public WarpsGui(Player player, Category category, String search) {
        this(player, category);
        this.search = search;
    }

    public WarpsGui(Player player, Category category) {
        this(player);
        this.category = category;
    }

    public WarpsGui(Player player) {
        super(GUI, player);
        setGui(gui);
    }

    public static boolean reload() {
        return GUI.reload();
    }

    public void open() {
        open(1);
    }

    public void open(int page) { // todo: use search & category
        loadWarps().thenRun(() -> {
            updateTitle();
            gui.open(player, page);
        });
    }

    @Override
    public void updateTitle() {
        gui.updateTitle(StringUtils.formatToString(GUI.getString("title", ""), new HashMap<>(Map.of("%page%", "" + gui.getCurrentPageNum(), "%pages%", "" + Math.max(1, gui.getPagesNum())))));
    }

    public CompletableFuture<Void> loadWarps() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        AxPlayerWarps.getThreadedQueue().submit(() -> {
            gui.clearPageItems();
            for (Warp warp : WarpManager.getWarps()) {
                if (category != null && !Objects.equals(warp.getCategory(), category)) continue;
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
                    for (String s : description) {
                        lore.add(i, line.replace("%description%", s));
                    }
                }
                builder.setLore(Placeholders.parseList(warp, player, lore));
                if (icon == Material.PLAYER_HEAD) builder.setTextureValue(NMSHandlers.getNmsHandler().textures(player).getFirst());
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
