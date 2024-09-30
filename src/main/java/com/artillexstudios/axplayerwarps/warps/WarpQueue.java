package com.artillexstudios.axplayerwarps.warps;

import com.artillexstudios.axapi.utils.Cooldown;
import com.artillexstudios.axapi.utils.Pair;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.MESSAGEUTILS;

public class WarpQueue {
    private static final WeakHashMap<Player, Pair<Warp, Long>> queue = new WeakHashMap<>();
    private static final Cooldown<Player> bc = new Cooldown<>();
    private static ScheduledExecutorService service = null;

    public static void start() {
        if (service != null) service.shutdown();

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            long time = CONFIG.getLong("teleport-delay-seconds");
            try {
                for (Iterator<Map.Entry<Player, Pair<Warp, Long>>> it = queue.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Player, Pair<Warp, Long>> e = it.next();
                    if (bc.hasCooldown(e.getKey())) continue;
                    bc.addCooldown(e.getKey(), 1_000L);
                    long spent = (System.currentTimeMillis() - e.getValue().getValue()) / 1_000L;
                    if (spent >= time) {
                        e.getValue().getKey().completeTeleportPlayer(e.getKey());
                        it.remove();
                    } else {
                        MESSAGEUTILS.sendLang(e.getKey(), "teleport.in", Map.of("%seconds%", "" + (time - spent)));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    public static void addToQueue(Player player, Warp warp) {
        queue.put(player, new Pair<>(warp, System.currentTimeMillis()));
    }

    public static WeakHashMap<Player, Pair<Warp, Long>> getQueue() {
        return queue;
    }

    public static void stop() {
        if (service == null) return;
        service.shutdown();
    }
}
