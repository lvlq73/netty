package com.llq.netty.proxy;

import com.llq.netty.client.v1.IRpcClientV1;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lvlianqi
 * @description RpcProxy 动态代理实现 RPC 代理
 * @createDate 2019/11/27 22:17
 */
public class RpcProxyV1 {
    private String serverAddress;
    private IRpcClientV1 rpcClient;


    public RpcProxyV1(String serverAddress, IRpcClientV1 rpcClient) {
        this.serverAddress = serverAddress;
        this.rpcClient = rpcClient;

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        rpcClient.setHostAndPort(host, port);
    }

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
