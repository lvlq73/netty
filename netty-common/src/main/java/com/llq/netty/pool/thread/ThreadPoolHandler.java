package com.llq.netty.pool.thread;

import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程执行类
 * lvlianqi
 */
public class ThreadPoolHandler{

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolHandler.class);
    /**
     * 对象编码，没啥用，只是为了测试是不是创建了新的对象
     */
    private String code;

    public ThreadPoolHandler(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 对象池
     */
    private ObjectPool objectPool;

    public void setObjectPool(ObjectPool objectPool){
        this.objectPool = objectPool;
    }
    /**
     * 执行方法
     * @param data
     * @param threadPoolExecute
     */
    public void execute(Object data, final IThreadPoolExecute threadPoolExecute){
        try{
            threadPoolExecute.setData(data);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadPoolExecute.execute();
                    returnObject();
                }
            }).start();
        }catch (ClassCastException e){
            //System.out.println("参数类型错误:"+e.getMessage());
            logger.error("参数类型错误:"+e.getMessage());
        }
    }

    /**
     * 归还对象
     */
    private void returnObject(){
        try {
            objectPool.returnObject(this);
            //System.out.println("---------------------归还对象成功,对象编码："+code);
            logger.info("---------------------归还对象成功,对象编码："+code+"-----------------------------------");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
