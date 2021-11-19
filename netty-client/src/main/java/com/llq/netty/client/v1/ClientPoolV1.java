package com.llq.netty.client.v1;

import com.llq.netty.client.RpcClientConnect;
import com.llq.netty.discovery.Address;
import com.llq.netty.discovery.ServiceDiscovery;
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
public class ClientPoolV1 implements IRpcClientV1 {

    private static final PoolWrapper poolWrapper = new PoolWrapper();
    private List<RpcClientConnect> rpcClientConnects = new ArrayList<>();
    private int currentIndex;
    private int totalServer;

    public ClientPoolV1() {
        load();
    }

    //加载注册中心地址
    public void load() {
        if (rpcClientConnects.size() != 0) {
            rpcClientConnects.clear();
        }
        for (Address address : ServiceDiscovery.getServiceAddress()) {
            rpcClientConnects.add(new RpcClientConnect(address.host, address.port).initBuild());
        }
        totalServer = rpcClientConnects.size();
        currentIndex = totalServer - 1;
    }

    //简单轮询，不考虑并发
    public RpcClientConnect round() {
        /*if (rpcClientConnects.size() != ServiceDiscovery.getServiceAddress().size()){
            load();
        }*/
        currentIndex = (currentIndex + 1) % totalServer;
        return rpcClientConnects.get(currentIndex);
    }

    @Override
    public RpcResponseBody send(RpcRequestBody requestBody) throws InterruptedException, ExecutionException {
        RpcClientV1 client = poolWrapper.getObject(RpcClientV1.class, null);
        RpcClientConnect rpcClientConnect = round();
        RpcResponseBody response = client.send(rpcClientConnect, requestBody);
        client.returnObject(); //归还对象到对象池
        return response;
    }
}
