package com.llq.netty.service;

import com.google.common.util.concurrent.Uninterruptibles;
import com.llq.netty.api.IHelloService;
import com.llq.netty.scan.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author lvlianqi
 * @description HelloServiceImpl 测试用
 * @createDate 2019/12/12 20:57
 */
@RpcService(IHelloService.class)
@Deprecated
public class HelloServiceImpl implements IHelloService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public String hello(String name) {
        logger.info("----------------接收："+name);
        return "Hello! " + name;
    }

    @Override
    public int sum(int one, int two) {
        logger.info("----------------接收："+ one + "+" + two);
        //Uninterruptibles.sleepUninterruptibly(3 , TimeUnit.SECONDS);
        return one + two;
    }
}
