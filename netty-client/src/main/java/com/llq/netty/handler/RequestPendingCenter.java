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

    private static Map<Long, RpcResultFuture> map = new ConcurrentHashMap<>();

    private RequestPendingCenter() {

    }

    public static void add(Long streamId, RpcResultFuture future){
        map.put(streamId, future);
    }

    public static void set(Long streamId, RpcResponseBody response){
        RpcResultFuture operationResultFuture = map.get(streamId);
        if (operationResultFuture != null) {
            operationResultFuture.setSuccess(response);
            map.remove(streamId);
        }
     }
}
