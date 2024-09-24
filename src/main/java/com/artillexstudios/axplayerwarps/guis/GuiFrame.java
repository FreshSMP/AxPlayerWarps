package com.artillexstudios.axplayerwarps.guis;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.Placeholder;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.guis.actions.Actions;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.warps.Warp;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GuiFrame {
    protected final Config file;
    protected BaseGui gui;
    protected Player player;
    private Placeholder placeholder;
    private @Nullable Warp warp;

    public BaseGui getGui() {
        return gui;
    }

    public GuiFrame(Config file, Player player) {
        this.file = file;
        this.player = player;
        this.placeholder = new Placeholder((player1, s) -> s);
    }

    public GuiFrame(Config file, Player player, Placeholder placeholder) {
        this(file, player);
        this.placeholder = placeholder;
    }

    public void setPlaceholder(Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    public static IntArrayList getSlots(List<String> s) {
        final IntArrayList slots = new IntArrayList();

        for (String str : s) {
            if (NumberUtils.isInt(str)) {
                slots.add(Integer.parseInt(str));
            } else {
                String[] split = str.split("-");
                int min = Integer.parseInt(split[0]);
                int max = Integer.parseInt(split[1]);
                for (int i = min; i <= max; i++) {
                    slots.add(i);
                }
            }
        }

        return slots;
    }

    public IntArrayList getSlots(String route) {
        final List<String> slots = file.getBackingDocument().getStringList(route + ".slot");
        return getSlots(slots.isEmpty() ? List.of(file.getString(route + ".slot")) : slots);
    }

    public void setGui(BaseGui gui) {
        this.gui = gui;
        for (String str : file.getBackingDocument().getRoutesAsStrings(false)) createItem(str);
    }

    public void setWarp(Warp warp) {
        this.warp = warp;
    }

    @NotNull
    public Config getFile() {
        return file;
    }

    protected ItemStack buildItem(@NotNull String key) {
        return buildItem(key, Map.of());
    }

    protected ItemStack buildItem(@NotNull String key, Map<String, String> replacements) {
        final Section section = file.getSection(key);
        final ItemBuilder builder = new ItemBuilder(section);

        section.getOptionalString("name").ifPresent((name) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                name = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, name);
            }
            name = placeholder.parse(player, name);
            builder.setName(name, replacements);
        });

        section.getOptionalStringList("lore").ifPresent((lore) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                lore = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, lore);
            }
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, placeholder.parse(player, lore.get(i)));
            }
            builder.setLore(lore, replacements);
        });

        ItemStack item = builder.get();
        if (section.getOptionalString("texture").isEmpty() && item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            item.setItemMeta(skullMeta);
        }

        if (warp != null) item = Placeholders.parseItem(warp, player, item);
        return item;
    }

    protected GuiItem createItem(@NotNull String route) {
        return createItem(route, event -> {
            Actions.run(player, this, file.getStringList(route + ".actions"));
        }, Map.of());
    }

    protected GuiItem createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action) {
        return createItem(route, action, Map.of());
    }

    protected GuiItem createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements) {
        if (file.getString(route + ".slot") == null && file.getStringList(route + ".slot").isEmpty()) return null;
        final List<String> slots = file.getBackingDocument().getStringList(route + ".slot");
        return createItem(route, action, replacements, getSlots(slots.isEmpty() ? List.of(file.getString(route + ".slot")) : slots));
    }

    protected GuiItem createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action, ItemStack item) {
        if (file.getString(route + ".slot") == null && file.getStringList(route + ".slot").isEmpty()) return null;
        final List<String> slots = file.getBackingDocument().getStringList(route + ".slot");
        final GuiItem guiItem = new GuiItem(item, action);
        gui.setItem(getSlots(slots.isEmpty() ? List.of(file.getString(route + ".slot")) : slots), guiItem);
        return guiItem;
    }

    protected GuiItem createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements, IntArrayList slots) {
        final GuiItem guiItem = new GuiItem(buildItem(route, replacements), action);
        gui.setItem(slots, guiItem);
        return guiItem;
    }

    protected void createItem(@NotNull String route, @NotNull ItemStack item, @Nullable GuiAction<InventoryClickEvent> action) {
        if (file.getSection(route) == null) return;
        final GuiItem guiItem = new GuiItem(item, action);
        final List<String> slots = file.getBackingDocument().getStringList(route + ".slot");
        gui.setItem(getSlots(slots.isEmpty() ? List.of(file.getString(route + ".slot")) : slots), guiItem);
    }

    protected void extendLore(ItemStack item, String... lore) {
        final ItemMeta meta = item.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta.getLore() != null) newLore.addAll(meta.getLore());
        newLore.addAll(StringUtils.formatListToString(Arrays.asList(lore)));
        meta.setLore(newLore);
        item.setItemMeta(meta);
    }

    public void updateTitle() {
    }

    public void open() {

    }
}
