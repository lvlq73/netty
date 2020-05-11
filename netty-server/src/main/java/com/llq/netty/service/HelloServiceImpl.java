package com.llq.netty.service;

import com.llq.netty.api.IHelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvlianqi
 * @description HelloServiceImpl
 * @createDate 2019/12/12 20:57
 */
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
        return one + two;
    }
}
