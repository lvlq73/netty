package com.llq.netty.register.map;

import com.llq.netty.client.ClientPool;
import com.llq.netty.proxy.RpcProxy;
import com.llq.netty.scan.RpcApi;
import com.llq.netty.utils.RpcPropertiesUtil;
import org.reflections.Reflections;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author lvlianqi
 * @description 客户端扫描api并注册到map
 * @date 2021/11/27
 */
public class ApiMapRegister {

    // 存放接口名与代理对象之间的映射关系
    private static Map<String, Object> handlerMap = new HashMap<>();

    private ApiMapRegister() {

    }

    public static void register(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return;
        }
        register(Arrays.asList(packageName));
    }

    public static void register(List<String> packageNames) {
        if (CollectionUtils.isEmpty(packageNames)) {
            return;
        }
        for (String packageName : packageNames) {
            Reflections reflection = new Reflections(packageName);
            Set<Class<?>> classList = reflection.getTypesAnnotatedWith(RpcApi.class);
            for (Class<?> aClass : classList) {
                RpcApi rpcApi = aClass.getAnnotation(RpcApi.class);
                String interfaceName = rpcApi.alias();
                String host = RpcPropertiesUtil.getValue("netty.rpc.host");
                int port = RpcPropertiesUtil.getValue("netty.rpc.port", Integer.class);
                RpcProxy rpcProxy = new RpcProxy(new ClientPool(host, port));
                handlerMap.put(interfaceName, rpcProxy.create(aClass));
            }
        }
    }

    public static <T> T getBean(String key) {
        return (T) handlerMap.get(key);
    }

}
