package com.llq.netty.scan;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lvlianqi
 * @description
 * @date 2021/11/19
 */
public class ServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceFactory.class);

    // 存放接口名与服务对象之间的映射关系
    private static Map<String, Object> handlerMap = new HashMap<>();

    private ServiceFactory() {

    }

    public static void scanService(String packageName) {
        Reflections reflection = new Reflections(packageName);
        Set<Class<?>> classList = reflection.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> aClass : classList) {
            RpcService rpcService = aClass.getAnnotation(RpcService.class);
            String interfaceName = rpcService.value().getName();
            try {
                handlerMap.put(interfaceName, aClass.newInstance());
            } catch (InstantiationException e) {
                LOGGER.error("扫描服务类异常 InstantiationException", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("扫描服务类异常 IllegalAccessException", e);
            }
        }
    }

    public static <T> T getBean(String key) {
        return (T) handlerMap.get(key);
    }
}
