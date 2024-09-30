package com.artillexstudios.axplayerwarps.guis;

import java.util.concurrent.ScheduledExecutorService;

public class GuiUpdater {
    private static ScheduledExecutorService service = null;

    public static void start() {
//        if (service != null) service.shutdown();
//
//        service = Executors.newSingleThreadScheduledExecutor();
//        service.scheduleAtFixedRate(() -> {
//            try {
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void stop() {
        if (service == null) return;
        service.shutdown();
    }
}
