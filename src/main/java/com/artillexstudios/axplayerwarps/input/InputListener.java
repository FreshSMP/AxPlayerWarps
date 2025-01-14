package com.artillexstudios.axplayerwarps.input;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.WeakHashMap;
import java.util.function.Consumer;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.INPUT;

public class InputListener implements Listener {
    public static WeakHashMap<Player, Consumer<String>> inputPlayers = new WeakHashMap<>();

    public static WeakHashMap<Player, Consumer<String>> getInputPlayers() {
        return inputPlayers;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Consumer<String> consumer = inputPlayers.remove(event.getPlayer());
        if (consumer == null) return;
        event.setCancelled(true);
        if (INPUT.getStringList("chat-cancel-words").contains(event.getMessage())) {
            consumer.accept("");
            return;
        }
        consumer.accept(event.getMessage());
    }
}
