package de.thu.tpro.android4bikes.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Processor {
    private static Processor instance;
    private ExecutorService executorService;

    private Processor() {
        executorService = Executors.newFixedThreadPool(8);
    }

    public static Processor getInstance() {
        if (instance == null) {
            instance = new Processor();
        }
        return instance;
    }

    public void startRunnable(Runnable runnable) {
        executorService.execute(runnable);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
