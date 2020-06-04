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
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author lvlianqi
 * @description 初始化建立连接
 * @createDate 2020/5/19
 */
public class RpcClientConnect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientConnect.class);

    private String host;
    private int port;
    //public final RequestPendingCenter REQUEST_PENDING_CENTER = new RequestPendingCenter();
    //private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    private CountDownLatch signal = new CountDownLatch(1);
    private ChannelFuture channelFuture;

    public RpcClientConnect(String host, int port){
        this.host = host;
        this.port = port;
    }

    public ChannelFuture getChannelFuture() throws InterruptedException {
        //Netty服务端链路没有建立完毕之前，先挂起等待
        /*if (channelFuture == null) {
            signal.await();
        }*/
        return channelFuture;
    }

    public RpcClientConnect initBuild(){
        init();
        return this;
    }

    public void init() {
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

                    pipeline.addLast("protocolDecoder", new RpcProtocolDecoder());
                    pipeline.addLast("protocolEncoder", new RpcProtocolEncoder());
                    pipeline.addLast("dispatcherHandler", new ResponseDispatcherHandler());

                    pipeline.addLast(keepaliveHandler);
                    //pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect(host, port);

            channelFuture.sync();

            this.channelFuture = channelFuture;

            //唤醒等待客户端RPC线程
            //signal.countDown();
        } catch (InterruptedException e) {
            LOGGER.error("初始化客户端或建立连接异常", e);
        }
    }
}
