package com.llq.netty.proxy;

import com.llq.netty.client.IRpcClient;
import com.llq.netty.client.RpcClient;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.pool.common.PoolUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lvlianqi
 * @description RpcProxy 动态代理实现 RPC 代理
 * @createDate 2019/11/27 22:17
 */
public class RpcProxy {
    private String serverAddress;
    //private ServiceDiscovery serviceDiscovery;
    private IRpcClient rpcClient;

    public RpcProxy(String serverAddress, IRpcClient rpcClient) {
        this.serverAddress = serverAddress;
        this.rpcClient = rpcClient;
    }

    /*public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }*/

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        ProxyPool proxyPool = PoolUtil.getObject(ProxyPool.class);
        try {
            return (T) proxyPool.getProxyObject(serverAddress, rpcClient, interfaceClass);
        } finally {
            //获取后直接归还，让其他线程使用，实现一个代理对象可以处理多个线程请求，减少对象的开销
            proxyPool.returnObject();
        }
    }
}
