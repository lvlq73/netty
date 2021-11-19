package com.llq.netty.client;

import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;

import java.util.concurrent.ExecutionException;

/**
 * @author lvlianqi
 * @description 客户端测试，不用对象池调用
 * @createDate 2020/5/5
 */
public class Client implements IRpcClient {

    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponseBody send(RpcRequestBody requestBody) throws InterruptedException, ExecutionException {
        // 初始化 RPC 客户端
        //RpcClient client = new RpcClient();
        RpcClient client = new RpcClient(host, port);
        RpcResponseBody response = client.send(requestBody);
        return response;
    }
}
