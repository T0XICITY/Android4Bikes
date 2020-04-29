package de.thu.tpro.android4bikes.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.thu.tpro.android4bikes.services.UploadRunnable;

public class Processor {
    private static Processor instance;
    private ExecutorService executorService;
    private Timer timer_uploadTask;

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

    //TODO: Review
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

    public void scheduleUploadTask() {
        timer_uploadTask = scheduleTask(new UploadRunnable(), 10, 1000);
    }

    public void stopUploadTask() {
        if (timer_uploadTask != null) {
            cancelTimerTask(timer_uploadTask);
            timer_uploadTask.purge();
            timer_uploadTask = null;
        }
    }

    /**
     * stop scheduled TimerTask
     *
     * @param timer associated timer
     */
    public void cancelTimerTask(Timer timer) {
        //TODO: Review
        timer.cancel();
        timer.purge();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
