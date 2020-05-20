package com.llq.netty.test;

import com.llq.netty.api.IHelloService;
import com.llq.netty.client.Client;
import com.llq.netty.client.ClientPool;
import com.llq.netty.proxy.RpcProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lvlianqi
 * @description 测试
 * @createDate 2020/4/27
 */
public class Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws InterruptedException {
        //并行度10000
        int parallel = 10000;
        //调用成功计数
        AtomicInteger successCount = new AtomicInteger();
        //调用失败计数
        AtomicInteger failCount = new AtomicInteger();
        //开始计时
        StopWatch sw = new StopWatch();
        sw.start();
        //用于挂起所有线程，并发执行
        CountDownLatch signal = new CountDownLatch(1);
        //所有线程执行完后才往下走
        CountDownLatch finish = new CountDownLatch(parallel);
        //客户端没有运用对象池
        //RpcProxy proxy = new RpcProxy("127.0.0.1:8000", new Client());
        //客户端运用对象池
        RpcProxy proxy = new RpcProxy("127.0.0.1:8000", new ClientPool());
        LOGGER.info("并发数据开始准备----------------");
        for (int i = 0; i < parallel; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        signal.await();
                        //long start = System.currentTimeMillis();
                        IHelloService helloService = proxy.create(IHelloService.class);
                        //String result = helloService.hello("test"+ finalI);
                        int result = helloService.sum(finalI, finalI * (int)(Math.random() * 10));
                        System.out.println(result);
                        successCount.incrementAndGet();
                        // long end = System.currentTimeMillis();
                        //  System.out.println("耗时："+ (end-start)+"毫秒-----------结果："+result);
                    } catch (Throwable e) {
                        failCount.incrementAndGet();
                    } finally {
                        finish.countDown();
                    }
                }
            }).start();
        }
        LOGGER.info("并发数据准备完成----------------");
        //10000个并发线程瞬间发起请求操作
        signal.countDown();
        finish.await();

        sw.stop();

        String tip = String.format("RPC调用总共耗时: [%s] 毫秒,成功 [%s],失败[%s]", sw.getTotalTimeMillis(), successCount, failCount);
        System.out.println(tip);
        LOGGER.info(tip);

        System.exit(0);
    }
}
