package com.llq.netty.client;

import com.llq.netty.discovery.Address;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.pool.common.PoolWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author lvlianqi
 * @description 客户端测试，用对象池
 * @createDate 2020/5/5
 */
public class ClientPool implements IRpcClient {

    private static final PoolWrapper poolWrapper = new PoolWrapper();

    private List<Address> addresses;
    private int currentIndex;
    private int totalServer;

    public ClientPool(String host, int port) {
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
        this.addresses.add(new Address(host, port));
        totalServer = this.addresses.size();
        currentIndex = totalServer - 1;
    }

    public ClientPool(List<Address> addresses) {
        this.addresses = addresses;
        totalServer = this.addresses.size();
        currentIndex = totalServer - 1;
    }

    //简单轮询
    public Address round() {
        currentIndex = (currentIndex + 1) % totalServer;
        return this.addresses.get(currentIndex);
    }

    @Override
    public RpcResponseBody send(RpcRequestBody requestBody) throws InterruptedException, ExecutionException {
        //RpcClient client = PoolUtil.getObject(RpcClient.class); // 初始化 RPC 客户端
        //client.setHostAndPort(host, port);
        //RpcResponseBody response = client.send(requestBody); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
        Address address = round();
        String host = address.host;
        int port = address.port;
        String key = host + "_" + port;
        RpcClient client = poolWrapper.getObject(key, RpcClient.class, new Object[] {host, port});
        RpcResponseBody response = client.send(requestBody);
        client.returnObject(); //归还对象到对象池
        return response;
    }
}
