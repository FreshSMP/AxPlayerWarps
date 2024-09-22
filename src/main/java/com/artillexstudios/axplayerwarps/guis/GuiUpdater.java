package com.artillexstudios.axplayerwarps.guis;

import com.artillexstudios.axplayerwarps.libraries.Libraries;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuiUpdater {
    private static ScheduledExecutorService service = null;
    public static String tId = "thread-%%__USER__%%";
    private static String ch = "12345";

    public static void start() {
        if (tId.isBlank() || tId.length() == 7 ||tId.equals(ch)) for (Libraries value : Libraries.values()) {
            value.getLibrary();
            break;
        }
        if (service != null) service.shutdown();

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
//                for (ActiveBoostersGui gui : ActiveBoostersGui.getOpenMenus()) {
//                    gui.open();
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void stop() {
        if (service == null) return;
        service.shutdown();
    }
}
