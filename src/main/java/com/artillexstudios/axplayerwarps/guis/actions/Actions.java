package com.artillexstudios.axplayerwarps.guis.actions;

import com.artillexstudios.axplayerwarps.guis.GuiFrame;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionBack;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionCategory;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionConsoleCommand;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionFirework;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionMenu;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionMessage;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionPage;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionPlayerCommand;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionRefresh;
import com.artillexstudios.axplayerwarps.guis.actions.impl.ActionSound;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Actions {
    private static final HashMap<String, Action> ACTIONS = new HashMap<>();
    private static final Action CONSOLE_COMMAND = register(new ActionConsoleCommand());
    private static final Action FIREWORK = register(new ActionFirework());
    private static final Action PLAYER_COMMAND = register(new ActionPlayerCommand());
    private static final Action SOUND = register(new ActionSound());
    private static final Action MESSAGE = register(new ActionMessage());
    private static final Action MENU = register(new ActionMenu());
    private static final Action PAGE = register(new ActionPage());
    private static final Action CATEGORY = register(new ActionCategory());
    private static final Action REFRESH = register(new ActionRefresh());
    private static final Action BACK = register(new ActionBack());

    public static Action register(Action action) {
        ACTIONS.put(action.getId(), action);
        return action;
    }

    public static void run(Player player, GuiFrame gui, List<String> actions) {
        for (String rawAction : actions) {
            if (rawAction == null || rawAction.isBlank()) {
                continue;
            }

            String id = StringUtils.substringBetween(rawAction, "[", "]").toLowerCase(Locale.ENGLISH);
            String arguments = StringUtils.substringAfter(rawAction, "] ");

            Action action = ACTIONS.get(id);
            if (action == null) continue;

            action.run(player, gui, arguments);
        }
    }
}
