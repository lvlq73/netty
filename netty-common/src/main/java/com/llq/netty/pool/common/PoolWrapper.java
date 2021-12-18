package com.llq.netty.pool.common;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlianqi
 * @description 对象池包装类
 * @createDate 2020/4/28
 */
public class PoolWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolWrapper.class);

    private static final int maxTotal = 5;// 最大线程数
    private static final int maxIdle = 5;// 最大闲置线程
    private static final int minIdle = 0;// 最小闲置线程
    //private static final int maxWaitMillis = 10000;// 最大等待时间
    /**
     * apache 封装的对象池
     */
    private Map<String, ObjectPool<PoolObject>> objectPools = new HashMap<>();
    /**
     * 对象池配置
     */
    public GenericObjectPoolConfig objectPoolConfig;

    public PoolWrapper() {
        this.objectPoolConfig = new GenericObjectPoolConfig();
        this.objectPoolConfig.setMaxTotal(maxTotal);// 最大线程
        this.objectPoolConfig.setMaxIdle(maxIdle);// 最大闲置线程
        this.objectPoolConfig.setMinIdle(minIdle);// 最小闲置线程
        //this.objectPoolConfig.setMaxWaitMillis(maxWaitMillis);// 最大等待时间
    }

    public PoolWrapper(GenericObjectPoolConfig objectPoolConfig) {
        this.objectPoolConfig = objectPoolConfig;
    }

    /**
     * 从对象池获取对象
     * @param clazz
     * @param params 构造函数参数，如果没有穿null
     * @param <T>
     * @return
     */
    public <T extends PoolObject> T getObject(Class<T> clazz, Object[] params) {
        return getObject(null, clazz, params);
    }
    /**
     * 从对象池获取对象
     * @param name 自定义名称
     * @param clazz 对象类型
     * @param params 构造函数参数，如果没有穿null
     * @param <T>
     * @return
     */
    public <T extends PoolObject> T getObject(String name, Class<T> clazz, Object[] params){
        ObjectPool<PoolObject> objectPool = getObjectPool(name, clazz, params);
        PoolObject poolObject = null;
        try {
            poolObject = objectPool.borrowObject();// 从线程池获取对象，
            //System.out.println("---------------------获取对象成功,对象编码："+threadPoolHandler.getCode());
            //LOGGER.info("---------------------获取对象成功,对象名：{}对象编码：{}-----------------------------------", poolObject.getClass().getSimpleName(), poolObject.getCode());
            poolObject.setObjectPool(objectPool);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return (T) poolObject;
    }

    /**
     * apache自己封装的对象池
     * @return
     */
    private ObjectPool getObjectPool(String name, Class<?> clazz, Object[] params){
        String className = StringUtils.isEmpty(name) ? clazz.getName() : name;
        ObjectPool<PoolObject> objectPool = null;
        if(!objectPools.containsKey(className)){
            synchronized (objectPools.getClass()) {
                if(!objectPools.containsKey(className)){
                    PoolFactory poolFactory = new PoolFactory(clazz, params);
                    objectPool = new GenericObjectPool<PoolObject>(poolFactory, this.objectPoolConfig);
                    objectPools.put(className, objectPool);
                } else {
                    objectPool = objectPools.get(className);
                }
            }
        }else{
            objectPool = objectPools.get(className);
        }
        return objectPool;
    }

    public void remove(String name) {
        objectPools.remove(name);
    }
}
