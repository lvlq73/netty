package com.llq.netty.register.spring;

import com.llq.netty.client.ClientPool;
import com.llq.netty.proxy.RpcProxy;
import com.llq.netty.scan.RpcApi;
import com.llq.netty.utils.RpcPropertiesUtil;
import com.llq.registry.api.DefaultServiceDiscovery;
import com.llq.registry.api.IServiceDiscovery;
import com.llq.registry.pojo.Address;
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

    private IServiceDiscovery serviceDiscovery = null;
    private static String URL = RpcPropertiesUtil.getValue("netty.rpc.registry.url");
    private List<String> packageNames = new ArrayList<>();

    public ApiSpringRegister(String packageName) {
        this(packageName, new DefaultServiceDiscovery(URL));
    }

    public ApiSpringRegister(String packageName, IServiceDiscovery serviceDiscovery) {
        packageNames.add(packageName);
        this.serviceDiscovery = serviceDiscovery;
    }

    public ApiSpringRegister(List<String> packageNames) {
        this(packageNames, new DefaultServiceDiscovery(URL));
    }

    public ApiSpringRegister(List<String> packageNames, IServiceDiscovery serviceDiscovery) {
        this.packageNames = packageNames;
        this.serviceDiscovery = serviceDiscovery;
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
                String serviceId = rpcApi.serviceId();
                List<Address> addresses = serviceDiscovery.getService(serviceId);
                RpcProxy rpcProxy = new RpcProxy(new ClientPool(addresses));
                beanFactory.registerSingleton(interfaceName, rpcProxy.create(aClass));
            }
        }

    }
}
