package com.llq.netty.client;

import com.llq.netty.codec.RpcFrameDecoder;
import com.llq.netty.codec.RpcFrameEncoder;
import com.llq.netty.codec.RpcProtocolDecoder;
import com.llq.netty.codec.RpcProtocolEncoder;
import com.llq.netty.handler.ClientIdleCheckHandler;
import com.llq.netty.handler.KeepaliveHandler;
import com.llq.netty.handler.RequestPendingCenter;
import com.llq.netty.handler.ResponseDispatcherHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lvlianqi
 * @description
 * @createDate 2020/5/19
 */
public class RpcClientInit implements Runnable{

    private String host;
    private int port;
    public static final RequestPendingCenter REQUEST_PENDING_CENTER = new RequestPendingCenter();
    //private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private static final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    CountDownLatch signal = new CountDownLatch(1);
    private ChannelFuture channelFuture;

    public RpcClientInit(String host, int port){
        this.host = host;
        this.port = port;
    }

    public ChannelFuture getChannelFuture() throws InterruptedException {
        //Netty服务端链路没有建立完毕之前，先挂起等待
        if (channelFuture == null) {
            signal.await();
        }
        return channelFuture;
    }

    @Override
    public void run() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        try {
            bootstrap.group(GROUP);

            KeepaliveHandler keepaliveHandler = new KeepaliveHandler();
            //bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new ClientIdleCheckHandler());

                    pipeline.addLast("frameDecoder", new RpcFrameDecoder());
                    pipeline.addLast("frameEncoder", new RpcFrameEncoder());

                    pipeline.addLast("protocolEncoder", new RpcProtocolEncoder());
                    pipeline.addLast("protocolDecoder", new RpcProtocolDecoder());

                    pipeline.addLast("dispatcherHandler", new ResponseDispatcherHandler(REQUEST_PENDING_CENTER));

                    pipeline.addLast(keepaliveHandler);
                    //pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect(host, port);

            channelFuture.sync();

            this.channelFuture = channelFuture;

            //唤醒等待客户端RPC线程
            signal.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
