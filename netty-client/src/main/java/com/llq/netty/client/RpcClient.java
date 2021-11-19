package com.llq.netty.client;

import com.llq.netty.entity.ResultVo;
import com.llq.netty.entity.RpcMessage;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.handler.RequestPendingCenter;
import com.llq.netty.handler.RpcResultFuture;
import com.llq.netty.pool.common.PoolObject;
import com.llq.netty.utils.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author lvlianqi
 * @description rpc 客户端
 * @createDate 2020/4/27
 */
public class RpcClient extends PoolObject{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private RpcClientConnect rpcClientConnect;

    public RpcClient(String host, Integer port) {
        rpcClientConnect = new RpcClientConnect(host, port);
        rpcClientConnect.init();
    }

    /**
     * 发送请求
     * @param requestBody
     * @return RpcResponseBody
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public RpcResponseBody send(RpcRequestBody requestBody) throws InterruptedException, ExecutionException {
        try {
            //组装数据
            long streamId = IdUtil.nextId();
            RpcMessage<RpcRequestBody> request = new RpcMessage<>(streamId, requestBody);
            //添加future到请求等待中心
            RpcResultFuture rpcResultFuture = new RpcResultFuture();
            RequestPendingCenter.add(streamId, rpcResultFuture);

            rpcClientConnect.getChannelFuture().channel().writeAndFlush(request);

            RpcResponseBody responseBody = rpcResultFuture.get(6, TimeUnit.SECONDS);
            /*if (responseBody != null) {
                channelFuture.channel().closeFuture().sync();
            }*/
            return responseBody;
        } catch (TimeoutException e) {
            LOGGER.error("请求超时");
            return ResultVo.error("请求超时", e);
        } finally {
            //GROUP.shutdownGracefully();
            //returnObject();
        }
    }
}
