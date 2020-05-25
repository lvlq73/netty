package com.llq.netty.handler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lvlianqi
 * @description 业务线程池
 * @createDate 2020/5/8
 */
@Deprecated
public class BusinessThreadPool {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);

    public static void submit(Runnable runnable){
        EXECUTOR_SERVICE.execute(runnable);
    }
}
