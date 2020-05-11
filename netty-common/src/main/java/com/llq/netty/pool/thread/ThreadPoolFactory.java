package com.llq.netty.pool.thread;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.UUID;

/**
 * 线程池工厂类
 * lvlianqi
 */
public class ThreadPoolFactory extends BasePooledObjectFactory<ThreadPoolHandler> {

    /**
     * 创建对象
     * @return
     * @throws Exception
     */
    @Override
    public ThreadPoolHandler create() throws Exception {
        return new ThreadPoolHandler(UUID.randomUUID().toString());
    }

    /**
     * 包装对象
     * @param obj
     * @return
     */
    @Override
    public PooledObject<ThreadPoolHandler> wrap(ThreadPoolHandler obj) {
        return new DefaultPooledObject<ThreadPoolHandler>(obj);
    }
}
