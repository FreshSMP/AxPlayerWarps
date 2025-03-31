package com.artillexstudios.axplayerwarps.warps;

import com.artillexstudios.axapi.utils.Cooldown;
import com.artillexstudios.axapi.utils.Pair;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.MESSAGEUTILS;

public class WarpQueue {
    private static final Map<Player, Pair<Warp, Long>> queue = new ConcurrentHashMap<>();
    private static final Cooldown<Player> bc = new Cooldown<>();
    private static ScheduledExecutorService service = null;

    public static void start() {
        if (service != null) service.shutdown();

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            long time = CONFIG.getLong("teleport-delay-seconds");
            try {
                Map<Player, Pair<Warp, Long>> snapshot = Map.copyOf(queue);

                for (Map.Entry<Player, Pair<Warp, Long>> entry : snapshot.entrySet()) {
                    Player player = entry.getKey();
                    Pair<Warp, Long> warpPair = entry.getValue();

                    if (bc.hasCooldown(player)) continue;
                    bc.addCooldown(player, 1_000L);

                    long spent = (System.currentTimeMillis() - warpPair.getValue()) / 1_000L;
                    if (spent >= time) {
                        warpPair.getKey().completeTeleportPlayer(player);
                        queue.remove(player);
                    } else {
                        MESSAGEUTILS.sendLang(player, "teleport.in", Map.of("%seconds%", "" + (time - spent)));
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

    public static Map<Player, Pair<Warp, Long>> getQueue() {
        return queue;
    }

    public static void stop() {
        if (service == null) return;
        service.shutdown();
    }
}
