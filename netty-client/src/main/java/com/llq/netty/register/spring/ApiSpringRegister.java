package com.llq.netty.register.spring;

import com.llq.netty.client.ClientPool;
import com.llq.netty.proxy.RpcProxy;
import com.llq.netty.scan.RpcApi;
import com.llq.netty.utils.RpcPropertiesUtil;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lvlianqi
 * @description 客户端扫描api并注册到spring容器
 * @date 2021/11/27
 */
public class ApiSpringRegister implements BeanFactoryPostProcessor {

    private List<String> packageNames = new ArrayList<>();

    public ApiSpringRegister(String packageName) {
        packageNames.add(packageName);
    }

    public ApiSpringRegister(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
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
                beanFactory.registerSingleton(interfaceName, rpcProxy.create(aClass));
            }
        }

    }
}
