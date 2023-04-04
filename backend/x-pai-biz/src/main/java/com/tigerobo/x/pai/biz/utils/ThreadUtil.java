package com.tigerobo.x.pai.biz.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class ThreadUtil {
    public static ExecutorService executorService = Executors.newFixedThreadPool(30);

    public static ForkJoinPool detailPool = new ForkJoinPool(30);

    public static ExecutorService asyncDbExecutor = Executors.newCachedThreadPool();
}
