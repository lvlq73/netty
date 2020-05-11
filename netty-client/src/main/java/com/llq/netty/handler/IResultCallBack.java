package com.llq.netty.handler;

/**
 * @author lvlianqi
 * @description 发送回调
 * @createDate 2020/4/28
 */
@Deprecated
public interface IResultCallBack<T,R> {

    void setData(T t);

    R getResult();
}
