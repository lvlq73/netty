package com.llq.netty.pool.thread;

/**
 * 执行接口类
 * lvlianqi
 */
public interface IThreadPoolExecute<T> {

    /**
     * 用于传递参数到execute的方法里使用
     * @param t
     */
    public void setData(T t);
    /**
     * 执行方法
     */
    public void execute();
}
