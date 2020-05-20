package com.llq.netty.client;

import com.llq.netty.client.v1.RpcClientV1;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.pool.common.PoolUtil;

import java.util.concurrent.ExecutionException;

/**
 * @author lvlianqi
 * @description 客户端测试，用对象池
 * @createDate 2020/5/5
 */
public class ClientPool implements IRpcClient {

    private static volatile RpcClientInit rpcClientInit;

    private static RpcClientInit getRpcClientInit(String host, int port){
        if (rpcClientInit == null) {
            synchronized (RpcClientInit.class){
                if (rpcClientInit == null) {
                    rpcClientInit = new RpcClientInit(host, port);
                    rpcClientInit.init();
                    System.out.println("init...");
                }
            }
        }
        return rpcClientInit;
    }

    @Override
    public RpcResponseBody send(String host, int port, RpcRequestBody requestBody) throws InterruptedException, ExecutionException {
        //RpcClient client = PoolUtil.getObject(RpcClient.class); // 初始化 RPC 客户端
        //client.setHostAndPort(host, port);
        //RpcResponseBody response = client.send(requestBody); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

        RpcClientV1 client = PoolUtil.getObject(RpcClientV1.class);
        RpcClientInit rpcClientInit = getRpcClientInit(host, port);
        RpcResponseBody response = client.send(rpcClientInit, requestBody);
        client.returnObject(); //归还对象到对象池
        return response;
    }
}
