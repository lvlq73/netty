package com.llq.netty.client;

import com.llq.netty.entity.ResultVo;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.pool.common.PoolWrapper;
import com.llq.registry.client.ServiceAddressCache;
import com.llq.registry.pojo.Address;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author lvlianqi
 * @description 客户端测试，用对象池
 * @createDate 2020/5/5
 */
public class ClientPool implements IRpcClient {

    private static final PoolWrapper poolWrapper = new PoolWrapper();

//    private List<Address> addresses;
    private int currentIndex;
    private int totalServer;

    /*public ClientPool(String host, int port) {
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
    }*/
    public ClientPool(String host, int port) {
        this(Arrays.asList(new Address(host, port)));
    }

    public ClientPool(List<Address> addresses) {
        ServiceAddressCache.setAddressList(addresses);
        totalServer = addresses.size();
        currentIndex = totalServer - 1;
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
        if (client == null) {
            return ResultVo.error("客户端连接异常, RpcClient is null", new NullPointerException());
        }
        RpcResponseBody response = client.send(requestBody);
        client.returnObject(); //归还对象到对象池
        //如果超时异常移除该key的对象池
        if (response.getError() != null && response.getError() instanceof TimeoutException) {
            poolWrapper.remove(key);
        }
        return response;
    }

    //简单轮询
    private Address round() {
        List<Address> list = ServiceAddressCache.getList();
        if (list.size() == 0) {
            throw new RuntimeException("address list is null");
        }
        totalServer = list.size();
//        currentIndex = totalServer - 1;
        currentIndex = (currentIndex + 1) % totalServer;
        return list.get(currentIndex);
    }
}
