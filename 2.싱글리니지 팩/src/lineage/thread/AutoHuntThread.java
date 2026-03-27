package lineage.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.PcInstance;

public class AutoHuntThread implements Runnable {

    private static ScheduledExecutorService executorService;

    public static void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new AutoHuntThread(), 0, Common.THREAD_SLEEP, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            if (World.getPcList().isEmpty()) {
                return;
            }

            long time = System.currentTimeMillis();

            for (PcInstance pc : World.getPcList()) {
                if (pc.isAutoHunt) {
                    pc.toAutoHunt(time);
                }
            }
        } catch (Exception e) {
            lineage.share.System.printf("lineage.thread.AutoHuntThread.run()\r\n : %s\r\n", e.toString());
        }
    }

    public static void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}