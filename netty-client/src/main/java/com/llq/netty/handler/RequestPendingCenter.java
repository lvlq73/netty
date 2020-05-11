package com.llq.netty.handler;

import com.llq.netty.entity.RpcResponseBody;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvlianqi
 * @description 请求等待结果返回中心
 * @createDate 2020/4/26
 */
public class RequestPendingCenter {

    private Map<Long, RpcResultFuture> map = new ConcurrentHashMap<>();

    public void add(Long streamId, RpcResultFuture future){
        this.map.put(streamId, future);
    }

    public void set(Long streamId, RpcResponseBody response){
        RpcResultFuture operationResultFuture = this.map.get(streamId);
        if (operationResultFuture != null) {
            operationResultFuture.setSuccess(response);
            this.map.remove(streamId);
        }
     }
}
