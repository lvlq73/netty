package com.llq.netty.client;

import com.llq.netty.codec.RpcFrameDecoder;
import com.llq.netty.codec.RpcFrameEncoder;
import com.llq.netty.codec.RpcProtocolDecoder;
import com.llq.netty.codec.RpcProtocolEncoder;
import com.llq.netty.entity.ResultVo;
import com.llq.netty.entity.RpcMessage;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import com.llq.netty.handler.RequestPendingCenter;
import com.llq.netty.handler.ResponseDispatcherHandler;
import com.llq.netty.handler.RpcResultFuture;
import com.llq.netty.pool.common.PoolObject;
import com.llq.netty.utils.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    private String host;
    private int port;
    private static final RequestPendingCenter REQUEST_PENDING_CENTER = new RequestPendingCenter();
    private static final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    /*public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }*/

    public void setHostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 发送请求
     * @param requestBody
     * @return RpcResponseBody
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public RpcResponseBody send(RpcRequestBody requestBody) throws InterruptedException, ExecutionException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        try{
            bootstrap.group(GROUP);

            //bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("frameDecoder", new RpcFrameDecoder());
                    pipeline.addLast("frameEncoder", new RpcFrameEncoder());

                    pipeline.addLast("protocolEncoder", new RpcProtocolEncoder());
                    pipeline.addLast("protocolDecoder", new RpcProtocolDecoder());

                    pipeline.addLast("dispatcherHandler", new ResponseDispatcherHandler(REQUEST_PENDING_CENTER));

                    //pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect(host, port);

            channelFuture.sync();

            //组装数据
            long streamId = IdUtil.nextId();
            RpcMessage<RpcRequestBody> request = new RpcMessage<>(streamId, requestBody);
            //添加future到请求等待中心
            RpcResultFuture rpcResultFuture = new RpcResultFuture();
            REQUEST_PENDING_CENTER.add(streamId, rpcResultFuture);

            channelFuture.channel().writeAndFlush(request);

            RpcResponseBody responseBody = rpcResultFuture.get(6, TimeUnit.SECONDS);
            if (responseBody != null) {
                channelFuture.channel().closeFuture().sync();
            }
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
