package de.thu.tpro.android4bikes.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Processor {
    private static Processor instance;
    private ExecutorService executorService;

    private Processor() {
        /*
        Der CachedThreadPool bestimmt selbst mit wie vielen Threads er eine Liste von Runnables oder Callables abarbeitet.
        Er erzeugt bei Bedarf neue Threads oder verwendet auch alte wieder. Threads, die 60 Sekunden lang nicht verwendet wurden, werden aus dem Pool entfernt.
        Auf diese Weise werden Resourcen gespart und so die Performance verbessert. Der CachedThreadPool ist gut geeignet für viele kurzlebige Prozesse,
        hier hat er eine bessere Performance als andere ThreadPools.
        ~ https://www.straub.as/java/threads/cachedthreadpool.html
        */
        executorService = Executors.newCachedThreadPool();

        // todo: should we use cached thread pool or fixed one //executorService = Executors.newFixedThreadPool(8);
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
