package com.artillexstudios.axplayerwarps.placeholders;

import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.component.DataComponents;
import com.artillexstudios.axapi.items.component.type.ItemLore;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.database.impl.Base;
import com.artillexstudios.axplayerwarps.utils.FormatUtils;
import com.artillexstudios.axplayerwarps.utils.StarUtils;
import com.artillexstudios.axplayerwarps.utils.TimeUtils;
import com.artillexstudios.axplayerwarps.warps.Warp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;

public class Placeholders {
    public static final DecimalFormat df = new DecimalFormat("#.##");

    public static String parse(Base.AccessPlayer accessPlayer, @Nullable OfflinePlayer player, String t) {
        t = t.replace("%player%", accessPlayer.name());
        t = t.replace("%added-date%", TimeUtils.formatDate(accessPlayer.added()));

        return t;
    }

    public static List<String> parseList(Base.AccessPlayer accessPlayer, @Nullable OfflinePlayer player, List<String> t) {
        t.replaceAll(s -> parse(accessPlayer, player, s)); return t;
    }

    public static String parse(Warp warp, @Nullable OfflinePlayer player, String t) {
        t = t.replace("%name%", warp.getName());
        t = t.replace("%owner%", AxPlayerWarps.getDatabase().getPlayerName(warp.getOwner()));
        t = t.replace("%created%", TimeUtils.formatDate(warp.getCreated()));
        World world = warp.getLocation().getWorld();
        t = t.replace("%world%", world == null ? "---" : world.getName());
        t = t.replace("%x%", df.format(warp.getLocation().getX()));
        t = t.replace("%y%", df.format(warp.getLocation().getY()));
        t = t.replace("%z%", df.format(warp.getLocation().getZ()));
        t = t.replace("%yaw%", df.format(warp.getLocation().getYaw()));
        t = t.replace("%pitch%", df.format(warp.getLocation().getPitch()));
        if (warp.getCategory() != null)
            t = t.replace("%category%", CONFIG.getString("categories." + warp.getCategory().raw() + ".name"));
        else
            t = t.replace("%category%", LANG.getString("placeholders.no-category"));
        double price = warp.getCurrency() == null ? 0 : warp.getTeleportPrice();
        boolean isFree = warp.getCurrency() == null || warp.getTeleportPrice() == 0;
        t = t.replace("%price%", isFree ? LANG.getString("placeholders.free") : warp.getCurrency().getDisplayName().replace("%price%", df.format(price)));

        t = t.replace("%price-full%", FormatUtils.formatCurrency(warp.getCurrency(), warp.getTeleportPrice()));
        t = t.replace("%access%", LANG.getString("access." + warp.getAccess().name().toLowerCase()));

        double earned = warp.getEarnedMoney();
        t = t.replace("%earned_money%", FormatUtils.formatCurrency(warp.getCurrency(), earned));

        float rating = warp.getRating();
        t = t.replace("%rating_decimal%", df.format(rating));
        int starAm = Math.round(rating);
        t = t.replace("%rating_stars%", StarUtils.getFormatted(starAm, 5));
        t = t.replace("%rating_amount%", "" + warp.getRatingAmount());
        t = t.replace("%visitors%", "" + warp.getVisits());
        t = t.replace("%visitors_unique%", "" + warp.getUniqueVisits());
        t = t.replace("%favorites%", "" + warp.getFavorites());
        t = t.replace("%icon%", warp.getIcon().name().toLowerCase());

        return t;
    }

    public static List<String> parseList(Warp warp, @Nullable OfflinePlayer player, List<String> t) {
        t.replaceAll(s -> parse(warp, player, s)); return t;
    }

    public static ItemStack parseItem(Warp warp, @Nullable OfflinePlayer player, ItemStack it) {
        WrappedItemStack wrap = WrappedItemStack.wrap(it);
        Component nameComponent = wrap.get(DataComponents.customName());
        ItemLore itemLore = wrap.get(DataComponents.lore());
        wrap.set(DataComponents.customName(), parse(warp, player, nameComponent));
        wrap.set(DataComponents.lore(), new ItemLore(parseListComponent(warp, player, itemLore.lines())));
        return wrap.toBukkit();
    }

    public static final MiniMessage mm = MiniMessage.builder().build();
    public static Component parse(Warp warp, @Nullable OfflinePlayer player, Component t) {
        String string = mm.serialize(t);
        string = parse(warp, player, string);
        return StringUtils.format(string);
    }

    public static List<Component> parseListComponent(Warp warp, @Nullable OfflinePlayer player, List<Component> t) {
        List<String> strings = new ArrayList<>();
        for (Component s : t) {
            String string = mm.serialize(s);
            string = parse(warp, player, string);
            strings.add(string);
        }
        return StringUtils.formatList(strings);
    }
}
