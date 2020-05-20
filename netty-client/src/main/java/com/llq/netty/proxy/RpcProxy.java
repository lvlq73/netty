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
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    RpcRequestBody request = new RpcRequestBody(); // 创建并初始化 RPC 请求
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);

                    /*if (serviceDiscovery != null) {
                        serverAddress = serviceDiscovery.discover(); // 发现服务
                    }*/

                    String[] array = serverAddress.split(":");
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);

                    RpcResponseBody response = rpcClient.send(host, port, request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
                    if (response.getError() != null) {
                        throw response.getError();
                    } else {
                        return response.getResult();
                    }
                }
            }
        );
    }
}
