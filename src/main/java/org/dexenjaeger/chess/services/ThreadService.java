package org.dexenjaeger.chess.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadService {
    private final ExecutorService executorService;

    private ThreadService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public static ThreadService init(int nThreads) {
        return new ThreadService(Executors.newFixedThreadPool(nThreads));
    }

    public void run(Runnable runnable) {
        executorService.submit(runnable);
    }
}
