package com.llq.netty.api;

/**
 * @author lvlianqi
 * @description 测试rpc接口
 * @createDate 2019/11/27 21:14
 */
@Deprecated
public interface IHelloService {
    String hello(String name);

    int sum(int one, int two);
}
