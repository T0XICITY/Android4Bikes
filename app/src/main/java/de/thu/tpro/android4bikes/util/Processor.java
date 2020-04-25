package de.thu.tpro.android4bikes.util;

import java.util.Timer;
import java.util.TimerTask;
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

    public Timer scheduleTask(Runnable runnable, long delay, long period) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(runnable).start();
            }
        }, delay, period);
        return timer;
    }

    /**
     * stop scheduled TimerTask
     *
     * @param timer associated timer
     */
    public void cancelTimerTask(Timer timer) {
        timer.cancel();
        timer.purge();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
