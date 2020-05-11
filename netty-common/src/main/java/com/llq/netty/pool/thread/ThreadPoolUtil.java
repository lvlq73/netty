package com.llq.netty.pool.thread;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 * 常用的四种java自带线程池声明方法以及运用apache的对象池做一个线程池
 *  lvlianqi
 */
public class ThreadPoolUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);
    /**
     * 单一线程池
     */
    private static ExecutorService singleThread= null;
    /**
     * 固定大小线程池
     */
    private static ExecutorService fixedThreadPool = null;
    /**
     * 可缓存的线程池
     */
    private static ExecutorService cachedThreadPool = null;
    /**
     * 有核心线程数的线程池
     */
    private static ExecutorService scheduledThreadPool = null;
    /**
     * 单一线程池类型值
     */
    public static final  int SINGLE = 1;
    /**
     * 固定大小线程池类型值
     */
    public static final  int FIXED = 2;
    /**
     * 可缓存的线程池类型值
     */
    public static final  int CACHED= 3;
    /**
     * 有核心线程数的线程池类型值
     */
    public static final  int SCHEDULED= 4;

    private ThreadPoolUtil(){
    }

    /**
     * 创建一个单线程的线程池。这个线程池只有一个线程在工作，也就是相当于单线程串行执行所有任务。
     * 如果这个唯一的线程因为异常结束，那么会有一个新的线程来替代它。此线程池保证所有任务的执行顺序按照任务的提交顺序执行。
     * @return
     */
    public static ExecutorService getSingleThread(){
        if(singleThread == null){
            singleThread = Executors.newSingleThreadExecutor();
        }
        return singleThread;
    }

    /**
     * 创建固定大小的线程池。每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小。
     * 线程池的大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。
     * @param size 声明池大小
     * @return
     */
    public static ExecutorService getFixedThreadPool (int size){
        if(fixedThreadPool==null){
            fixedThreadPool = Executors.newFixedThreadPool(size);
        }
        return fixedThreadPool;
    }

    /**
     * 创建一个可缓存的线程池。如果线程池的大小超过了处理任务所需要的线程，那么就会回收部分空闲（60秒不执行任务）的线程，
     * 当任务数增加时，此线程池又可以智能的添加新线程来处理任务。
     * 此线程池不会对线程池大小做限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小。
     * @return
     */
    public static ExecutorService getCachedThreadPool (){
        if(cachedThreadPool==null){
            cachedThreadPool = Executors.newCachedThreadPool();
        }
        return fixedThreadPool;
    }

    /**
     * 创建一个线程池，它的核心线程数量是固定的，而非核心线程数是没有限制的，
     * 并且当非核心线程闲置时会被立即回收，它可安排给定延迟后运行命令或者定期地执行。
     * 这类线程池主要用于执行定时任务和具有固定周期的重复任务。
     * @param corePoolSize 核心线程数
     * @return
     */
    public static ExecutorService getScheduledThreadPool (int corePoolSize){
        if(scheduledThreadPool==null){
            scheduledThreadPool = Executors.newScheduledThreadPool(corePoolSize);
        }
        return scheduledThreadPool;
    }

    /**
     *  java自带线程池执行方法
     * @param poolType 声明线程池类型
     * @param runnable 执行的线程
     */
    public static void  executeJavaThreadPool(int poolType,Runnable runnable){
        executeJavaThreadPool(poolType,runnable,5,5);
    }

    /**
     *  java自带线程池执行方法
     * @param poolType 声明线程池类型  默认为 single 即值为1
     * @param runnable 执行的线程
     * @param maxPoolSize 最大线程池数 poolType 为 fixed即值为3时 填写，否则默认5
     * @param corePoolSize 核心线程池数 poolType 为 scheduled即值为4时 填写，否则默认5
     */
    public static void  executeJavaThreadPool(int poolType,Runnable runnable,Integer maxPoolSize,Integer corePoolSize){
        ExecutorService pool = null;
        switch (poolType){
            case ThreadPoolUtil.SINGLE : pool = getSingleThread() ;break ;
            case ThreadPoolUtil.FIXED : pool = getFixedThreadPool(maxPoolSize==null?5:maxPoolSize) ;break ;
            case ThreadPoolUtil.CACHED : pool = getCachedThreadPool() ;break ;
            case ThreadPoolUtil.SCHEDULED : pool = getScheduledThreadPool(corePoolSize==null?5:corePoolSize) ;break ;
            default: pool = getSingleThread() ; break;
        }
        pool.execute(runnable);
    }

    /**
     * 关闭java自带线程池
     */
    public static void shutdown(){
        if(singleThread !=null && !singleThread.isShutdown()){
            singleThread.shutdown();
        }
        if(fixedThreadPool !=null && !fixedThreadPool.isShutdown()){
            fixedThreadPool.shutdown();
        }
        if(cachedThreadPool !=null && !cachedThreadPool.isShutdown()){
            cachedThreadPool.shutdown();
        }
        if(scheduledThreadPool !=null && !scheduledThreadPool.isShutdown()){
            scheduledThreadPool.shutdown();
        }
    }

    //-----------------------------------------apache的线程池-----------------------------------------------------//

    /**
     * 线程池配置对象
     */
    private static GenericObjectPoolConfig objectPoolConfig = new GenericObjectPoolConfig();

    private static final int maxTotal = 5;// 最大线程数
    private static final int maxIdle = 5;// 最大闲置线程
    private static final int minIdle = 5;// 最小闲置线程

    static {
        objectPoolConfig.setMaxTotal(maxTotal);// 最大线程
        objectPoolConfig.setMaxIdle(maxIdle);// 最大闲置线程
        objectPoolConfig.setMinIdle(minIdle);// 最小闲置线程
    }

    /**
     * apache 封装的线程池
     */
    private static Map<String, ObjectPool<ThreadPoolHandler>> objectPools = new HashMap<>();

    /**
     * apache自己封装的对象池
     * @return
     */
    public static ObjectPool getApacheObjectPool (String className){
        ObjectPool<ThreadPoolHandler> objectPool = null;
        if(!objectPools.containsKey(className)){
            ThreadPoolFactory threadPoolFactory = new ThreadPoolFactory();
            objectPool = new GenericObjectPool<ThreadPoolHandler>(threadPoolFactory, objectPoolConfig);
            objectPools.put(className, objectPool);
        }else{
            objectPool = objectPools.get(className);
        }
        return objectPool;
    }

    /**
     * 清理所有apache线程池
     */
    public static void apachePoolClearAll(){
        if(!CollectionUtils.isEmpty(objectPools)){
            try {
               for(Map.Entry<String, ObjectPool<ThreadPoolHandler>> obj :objectPools.entrySet()){
                       obj.getValue().clear();
               }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 清理 apache对象池
     * @param  className 为 实现接口IThreadPoolExecute的className ,例如  threadPoolExecute.getClass().getName()
     */
    public static void apachePoolClear(String className){
        try {
            if(objectPools.containsKey(className)){
                objectPools.get(className).clear();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    /**
     * 关闭移除所有apache线程池
     */
    public static void apachePoolCloseAll(){
        if(!CollectionUtils.isEmpty(objectPools)){
            try {
                for(Map.Entry<String, ObjectPool<ThreadPoolHandler>> obj :objectPools.entrySet()){
                    obj.getValue().close();
                }
               Set<String> keys =  new HashSet<>(objectPools.keySet());
               for(String key : keys){
                   objectPools.remove(key);
               }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
    /**
     * 关闭 apache对象池
     * @param  className 为 实现接口IThreadPoolExecute的className ,例如  threadPoolExecute.getClass().getName()
     */
    public static void apachePoolClose(String className){
        try {
            if(objectPools.containsKey(className)){
                objectPools.get(className).close();
                objectPools.remove(className);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * apache线程池执行方法
     * @param data 参数传递
     * @param threadPoolExecute 线程执行的接口类
     */
    public static void executeApacheThreadPool(Object data, IThreadPoolExecute threadPoolExecute){
        ObjectPool<ThreadPoolHandler> objectPool = getApacheObjectPool(threadPoolExecute.getClass().getName());
        ThreadPoolHandler threadPoolHandler = null;
        try {
            threadPoolHandler = objectPool.borrowObject();// 从线程池获取对象，
            //System.out.println("---------------------创建对象成功,对象编码："+threadPoolHandler.getCode());
            logger.info("---------------------创建对象成功,对象编码："+threadPoolHandler.getCode()+"-----------------------------------");
            threadPoolHandler.setObjectPool(objectPool);
            threadPoolHandler.execute(data,threadPoolExecute);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args){
        for(int i =0;i<10; i++){
            ThreadPoolUtil.executeApacheThreadPool(i,new IThreadPoolExecute<Integer>() {
                private Integer i;

                @Override
                public void setData(Integer o) {
                    this.i = o;
                }

                @Override
                public void execute() {
                    System.out.println("执行"+ i);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        ThreadPoolUtil.apachePoolCloseAll();
    }
}
