package com.artillexstudios.axplayerwarps.guis;

import java.util.concurrent.ScheduledExecutorService;

public class GuiUpdater {
    private static ScheduledExecutorService service = null;

    public static void start() {
    }

    public static void stop() {
        if (service == null) return;
        service.shutdown();
    }
}
