package com.llq.netty.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lvlianqi
 * @description 原子long，获取id
 * @createDate 2019/11/27 22:00
 */
public final class IdUtil {

    private static final AtomicLong IDX = new AtomicLong();

    private IdUtil(){
        //no instance
    }

    public static long nextId(){
        return IDX.incrementAndGet();
    }

}
