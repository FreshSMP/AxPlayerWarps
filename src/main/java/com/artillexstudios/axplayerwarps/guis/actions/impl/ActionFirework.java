package com.artillexstudios.axplayerwarps.guis.actions.impl;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.Action;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

public class ActionFirework extends Action {
    public static final NamespacedKey FIREWORK_KEY = new NamespacedKey(AxPlayerWarps.getInstance(), "ax_firework");

    public ActionFirework() {
        super("firework");
    }

    @Override
    public void run(Player player, GuiFrame gui, String arguments) {
        String[] split = arguments.split("\\|");
        Color fireWorkColor = Color.fromRGB(Integer.valueOf(split[0].substring(1, 3), 16), Integer.valueOf(split[0].substring(3, 5), 16), Integer.valueOf(split[0].substring(5, 7), 16));
        Scheduler.get().run(task -> {
            Location location = player.getLocation();
            World world = location.getWorld();
            if (world == null) {
                return;
            }

            Firework fw = (Firework) world.spawnEntity(player.getLocation(), EntityType.fromName("firework_rocket"));
            FireworkMeta meta = fw.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.valueOf(split[1].toUpperCase(Locale.ENGLISH))).withColor(fireWorkColor).build());
            meta.setPower(0);
            fw.setFireworkMeta(meta);
            fw.getPersistentDataContainer().set(FIREWORK_KEY, PersistentDataType.BYTE, (byte) 0);
            fw.detonate();
        });
    }
}
