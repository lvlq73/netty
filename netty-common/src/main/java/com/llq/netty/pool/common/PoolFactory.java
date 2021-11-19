package com.llq.netty.pool.common;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author lvlianqi
 * @description 对象池工厂
 * @createDate 2020/4/28
 */
public class PoolFactory<T> extends BasePooledObjectFactory<T> {

    private Class<T> clazz;
    private Object[] params;

    public PoolFactory(Class<T> clazz, Object[] params) {
        this.clazz = clazz;
        this.params = params;
    }

    /**
     * 创建对象
     * @return
     * @throws Exception
     */
    @Override
    public T create() throws Exception {
        if (params != null) {
            Class[] classes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                classes[i] = params[i].getClass();
            }
            return clazz.getConstructor(classes).newInstance(params);
        } else {
            return clazz.newInstance();
        }
    }
    /**
     * 包装对象
     * @param obj
     * @return
     */
    @Override
    public PooledObject<T> wrap(T obj) {
        return new DefaultPooledObject<T>(obj);
    }
}
