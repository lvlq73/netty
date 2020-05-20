package com.llq.netty.client.v1;

import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;

import java.util.concurrent.ExecutionException;

/**
 * @author lvlianqi
 * @description
 * @createDate 2020/5/5
 */
public interface IRpcClientV1 {

    /**
     * 设置地址
     * @param host
     * @param port
     */
    void setHostAndPort(String host, int port);

    /**
     *  通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
     * @param requestBody 发送请求参数
     * @return RpcResponseBody
     * @throws InterruptedException
     * @throws ExecutionException
     */
    RpcResponseBody send(RpcRequestBody requestBody) throws InterruptedException, ExecutionException;
}
