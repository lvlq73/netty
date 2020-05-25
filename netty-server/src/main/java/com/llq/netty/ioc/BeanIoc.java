package com.llq.netty.ioc;

import com.llq.netty.api.IHelloService;
import com.llq.netty.service.HelloServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlianqi
 * @description
 * @createDate 2020/5/21
 */
public class BeanIoc {

    private BeanIoc() throws Exception {
        throw new Exception("static class , can't new class");
    }

    //存储接口实现类
    private static final Map<String, Object> handlerMap = new HashMap<>();

    /**
     * 初始化
     */
    public static void init() {
        handlerMap.put(IHelloService.class.getName(), new HelloServiceImpl());
    }

    public static <T> T getBean(String key) {
        return (T) handlerMap.get(key);
    }
}
