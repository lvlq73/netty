package com.llq.netty.client.v1;

import com.llq.netty.client.RpcClientInit;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.pool.common.PoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author lvlianqi
 * @description 客户端测试，用对象池
 * @createDate 2020/5/5
 */
public class ClientPoolV1 implements IRpcClientV1 {

    private List<RpcClientInit> rpcClientInits;
    private int currentIndex;
    private int totalServer;

    private String host;
    private int port;

    public void initRound() {
        rpcClientInits = new ArrayList<>();
        rpcClientInits.add(new RpcClientInit(host, port).initBuild());
        rpcClientInits.add(new RpcClientInit(host, port).initBuild());
        rpcClientInits.add(new RpcClientInit(host, port).initBuild());
        rpcClientInits.add(new RpcClientInit(host, port).initBuild());
        rpcClientInits.add(new RpcClientInit(host, port).initBuild());
        totalServer = rpcClientInits.size();
        currentIndex = totalServer - 1;
    }

    //简单轮询，不考虑并发
    public RpcClientInit round() {
        currentIndex = (currentIndex + 1) % totalServer;
        return rpcClientInits.get(currentIndex);
    }

    public void setHostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
        initRound();
    }

    @Override
    public RpcResponseBody send(RpcRequestBody requestBody) throws InterruptedException, ExecutionException {
        RpcClientV1 client = PoolUtil.getObject(RpcClientV1.class);
        RpcClientInit rpcClientInit = round();
        RpcResponseBody response = client.send(rpcClientInit, requestBody);
        client.returnObject(); //归还对象到对象池
        return response;
    }
}
