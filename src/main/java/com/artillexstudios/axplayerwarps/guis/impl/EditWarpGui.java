package com.artillexstudios.axplayerwarps.guis.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.component.DataComponents;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.Placeholder;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.category.Category;
import com.artillexstudios.axplayerwarps.category.CategoryManager;
import com.artillexstudios.axplayerwarps.enums.Access;
import com.artillexstudios.axplayerwarps.enums.AccessList;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Actions;
import com.artillexstudios.axplayerwarps.hooks.HookManager;
import com.artillexstudios.axplayerwarps.hooks.currency.CurrencyHook;
import com.artillexstudios.axplayerwarps.input.InputManager;
import com.artillexstudios.axplayerwarps.placeholders.Placeholders;
import com.artillexstudios.axplayerwarps.utils.StarUtils;
import com.artillexstudios.axplayerwarps.utils.WarpNameUtils;
import com.artillexstudios.axplayerwarps.warps.Warp;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.MESSAGEUTILS;

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
        super(GUI, player);
        setPlaceholder(new Placeholder((pl, s) -> {
            Integer rating = warp.getAllRatings().get(player.getUniqueId());
            s = s.replace("%given_rating_decimal%", rating == null ? "" : Placeholders.df.format(rating));
            s = s.replace("%given_rating_stars%", rating == null ? LANG.getString("placeholders.no-rating") : StarUtils.getFormatted(rating, 5));

            s = s.replace("%blacklisted%", "" + warp.getAccessList(AccessList.BLACKLIST).size());
            s = s.replace("%whitelisted%", "" + warp.getAccessList(AccessList.WHITELIST).size());
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
            AxPlayerWarps.getThreadedQueue().submit(() -> {
                AxPlayerWarps.getDatabase().updateWarp(warp);
                MESSAGEUTILS.sendLang(player, "editor.update-icon");
            });
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
                AxPlayerWarps.getThreadedQueue().submit(() -> {
                    AxPlayerWarps.getDatabase().updateWarp(warp);
                    MESSAGEUTILS.sendLang(player, "editor.remove-icon");
                });
                open();
                return;
            }
            InputManager.getInput(player, "rename", result -> {
                if (result.isBlank()) {
                    MESSAGEUTILS.sendLang(player, "errors.invalid-name");
                    open();
                    return;
                }

                switch (WarpNameUtils.isAllowed(result)) {
                    case DISALLOWED -> {
                        MESSAGEUTILS.sendLang(player, "errors.disallowed-name-blacklisted");
                        return;
                    }
                    case CONTAINS_SPACES -> {
                        MESSAGEUTILS.sendLang(player, "errors.disallowed-name-space");
                        return;
                    }
                    case INVALID_LENGTH -> {
                        MESSAGEUTILS.sendLang(player, "errors.disallowed-name-length");
                        return;
                    }
                }

                AxPlayerWarps.getThreadedQueue().submit(() -> {
                    if (!warp.setName(result.replace(" ", "_"))) {
                        MESSAGEUTILS.sendLang(player, "errors.name-exists");
                    } else {
                            AxPlayerWarps.getDatabase().updateWarp(warp);
                            MESSAGEUTILS.sendLang(player, "editor.update-name");
                    }
                    Scheduler.get().runAt(player.getLocation(), this::open);
                });
            });
        }, Map.of());
        ItemStack mt = guiItem.getItemStack();
        if (warp.getIcon() != null) mt.setType(warp.getIcon());
        guiItem.setItemStack(mt);

        createItem("location", event -> {
            Actions.run(player, this, file.getStringList("location.actions"));
            warp.setLocation(player.getLocation());
            AxPlayerWarps.getThreadedQueue().submit(() -> {
                AxPlayerWarps.getDatabase().updateWarp(warp);
                MESSAGEUTILS.sendLang(player, "editor.update-location");
            });
            open();
        }, Map.of());

        createItem("transfer", event -> {
            Actions.run(player, this, file.getStringList("transfer.actions"));
            warp.setLocation(player.getLocation());
            InputManager.getInput(player, "transfer", result -> {
                AxPlayerWarps.getThreadedQueue().submit(() -> {
                    UUID uuid = AxPlayerWarps.getDatabase().getUUIDFromName(result);
                    if (uuid == null) {
                        MESSAGEUTILS.sendLang(player, "errors.player-not-found");
                    } else {
                        Player transferTo = Bukkit.getPlayer(uuid);
                        warp.setOwner(uuid);
                        AxPlayerWarps.getDatabase().updateWarp(warp);
                        OfflinePlayer pl = Bukkit.getOfflinePlayer(uuid);
                        AxPlayerWarps.getDatabase().removeFromList(warp, AccessList.WHITELIST, pl);
                        AxPlayerWarps.getDatabase().removeFromList(warp, AccessList.BLACKLIST, pl);

                        if (transferTo != null)
                            MESSAGEUTILS.sendLang(transferTo, "editor.new-owner",
                                    Map.of("%player%", player.getName(), "%warp%", warp.getName()));

                        MESSAGEUTILS.sendLang(player, "editor.transferred", Map.of("%player%", pl.getName() == null ? "---" : pl.getName()));
                    }
                    Scheduler.get().runAt(player.getLocation(), player::closeInventory);
                });
            });
        }, Map.of());

        createItem("access", event -> {
            Actions.run(player, this, file.getStringList("access.actions"));
            Access currAccess = warp.getAccess();
            ArrayList<Access> accesses = new ArrayList<>(List.of(Access.values()));
            int idx = accesses.indexOf(currAccess);
            if (event.isLeftClick()) {
                idx++;
                if (idx >= accesses.size()) idx = 0;
            } else if (event.isRightClick()) {
                if (event.isShiftClick()) {
                    idx = 0;
                } else {
                    idx--;
                    if (idx < 0) idx = accesses.size() - 1;
                }
            }
            warp.setAccess(accesses.get(idx));
            AxPlayerWarps.getThreadedQueue().submit(() -> AxPlayerWarps.getDatabase().updateWarp(warp));
            open();
        }, Map.of());

        createItem("category", event -> {
            Actions.run(player, this, file.getStringList("category.actions"));
            Category category = warp.getCategory();
            ArrayList<Category> categories = new ArrayList<>(CategoryManager.getCategories().values());
            int idx = category == null ? -1 : categories.indexOf(category);
            if (event.isLeftClick()) {
                idx++;
                if (idx >= categories.size()) idx = 0;
            } else if (event.isRightClick()) {
                if (idx == -1) idx = 0;
                if (event.isShiftClick()) {
                    idx = -1;
                } else {
                    idx--;
                    if (idx < 0) idx = categories.size() - 1;
                }
            }
            warp.setCategory(idx == -1 ? null : categories.get(idx));
            AxPlayerWarps.getThreadedQueue().submit(() -> AxPlayerWarps.getDatabase().updateWarp(warp));
            open();
        }, Map.of());

        createItem("price", event -> {
            Actions.run(player, this, file.getStringList("price.actions"));
            if (warp.getEarnedMoney() > 0) warp.withdrawMoney();
            CurrencyHook currency = warp.getCurrency();
            ArrayList<CurrencyHook> currencies = HookManager.getCurrency();
            int idx = currency == null ? -1 : currencies.indexOf(currency);
            if (event.isLeftClick()) {
                if (event.isShiftClick()) {
                    InputManager.getInput(player, "price", result -> {
                        if (!NumberUtils.isInt(result)) {
                            MESSAGEUTILS.sendLang(player, "errors.not-a-number");
                        } else {
                            int price = Integer.parseInt(result);
                            if (price < 1) {
                                MESSAGEUTILS.sendLang(player, "errors.must-be-positive");
                                open();
                                return;
                            }
                            warp.setTeleportPrice(price);
                            AxPlayerWarps.getThreadedQueue().submit(() -> AxPlayerWarps.getDatabase().updateWarp(warp));
                        }
                        open();
                    });
                    return;
                }
                idx++;
                if (idx >= currencies.size()) idx = 0;
            } else if (event.isRightClick()) {
                if (idx == -1) idx = 0;
                if (event.isShiftClick()) {
                    idx = -1;
                    warp.setTeleportPrice(0);
                } else {
                    idx--;
                    if (idx < 0) idx = currencies.size() - 1;
                }
            }
            warp.setCurrency(idx == -1 ? null : currencies.get(idx));
            AxPlayerWarps.getThreadedQueue().submit(() -> AxPlayerWarps.getDatabase().updateWarp(warp));
            open();
        }, Map.of());

        createItem("delete", event -> {
            if (event.isShiftClick() && event.isRightClick()) {
                Actions.run(player, this, file.getStringList("delete.actions"));
                warp.delete();
                player.closeInventory();
            }
        }, Map.of());

        createItem("bank", event -> {
            Actions.run(player, this, file.getStringList("bank.actions"));
            warp.withdrawMoney();
            open();
        }, Map.of());

        ItemBuilder builder = new ItemBuilder(file.getSection("description"));
        WrappedItemStack wrap = WrappedItemStack.wrap(builder.get());
        List<String> lore = new ArrayList<>();
        String[] description = warp.getDescription().split("\n", CONFIG.getInt("warp-description.max-lines", 3));
        for (Component line : wrap.get(DataComponents.lore()).lines()) {
            String serialized = Placeholders.mm.serialize(line);
            if (serialized.contains("%description%")) {
                for (String s : description) {
                    lore.add(serialized.replace("%description%", s));
                }
                continue;
            }
            lore.add(serialized);
        }
        builder.setLore(lore);
        createItem("description", event -> {
            Actions.run(player, this, file.getStringList("description.actions"));
            var realDesc = warp.getRealDescription();
            List<String> desc = realDesc == null ? new ArrayList<>() : new ArrayList<>(Arrays.stream(realDesc.split("\n")).toList());
            if (event.isLeftClick()) {
                if (CONFIG.getInt("warp-description.max-lines") <= desc.size()) {
                    MESSAGEUTILS.sendLang(player, "errors.max-lines");
                    open();
                    return;
                }
                InputManager.getInput(player, "add-line", result -> {
                    desc.add(result);
                    warp.setDescription(desc);
                    AxPlayerWarps.getThreadedQueue().submit(() -> {
                        AxPlayerWarps.getDatabase().updateWarp(warp);
                        Scheduler.get().runAt(player.getLocation(), this::open);
                    });
                });
            } else if (event.isRightClick()) {
                if (event.isShiftClick()) {
                    desc.clear();
                    warp.setDescription(desc);
                    AxPlayerWarps.getThreadedQueue().submit(() -> AxPlayerWarps.getDatabase().updateWarp(warp));
                    open();
                    return;
                }
                if (!desc.isEmpty()) {
                    desc.remove(desc.size() - 1);
                }
                warp.setDescription(desc);
                AxPlayerWarps.getThreadedQueue().submit(() -> AxPlayerWarps.getDatabase().updateWarp(warp));
                open();
            }
        }, builder.get());

        createItem("whitelist", event -> {
            Actions.run(player, this, file.getStringList("whitelist.actions"));
            new WhitelistGui(player, warp, this).open();
        }, Map.of());

        createItem("blacklist", event -> {
            Actions.run(player, this, file.getStringList("blacklist.actions"));
            new BlacklistGui(player, warp, this).open();
        }, Map.of());

        gui.update();
        gui.open(player);
    }
}
