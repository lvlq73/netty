package com.llq.netty.proxy;

import com.llq.netty.client.IRpcClient;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.pool.common.PoolObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lvlianqi
 * @description 代理池对象
 * @createDate 2020/5/14
 */
@Deprecated
public class ProxyPool extends PoolObject {

    @SuppressWarnings("unchecked")
    public <T> T getProxyObject(String serverAddress, IRpcClient rpcClient, Class<?> interfaceClass) {
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

                        RpcResponseBody response = rpcClient.send(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
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
