package com.llq.netty.client;

import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.pool.common.PoolUtil;

import java.util.concurrent.ExecutionException;

/**
 * @author lvlianqi
 * @description 客户端测试，不用对象池调用
 * @createDate 2020/5/5
 */
public class Client implements IRpcClient {

    @Override
    public RpcResponseBody send(String host, int port, RpcRequestBody requestBody) throws InterruptedException, ExecutionException {
        RpcClient client = new RpcClient(); // 初始化 RPC 客户端
        client.setHostAndPort(host, port);
        RpcResponseBody response = client.send(requestBody);
        return response;
    }
}
