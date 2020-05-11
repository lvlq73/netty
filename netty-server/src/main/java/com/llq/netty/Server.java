package com.llq.netty;

import com.llq.netty.api.IHelloService;
import com.llq.netty.codec.RpcFrameDecoder;
import com.llq.netty.codec.RpcFrameEncoder;
import com.llq.netty.codec.RpcProtocolDecoder;
import com.llq.netty.codec.RpcProtocolEncoder;
import com.llq.netty.handler.MetricsHandler;
import com.llq.netty.handler.RpcHandler;
import com.llq.netty.service.HelloServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author lvlianqi
 * @description 服务端
 * @createDate 2020/4/26
 */
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    //存储接口实现类
    public static final Map<String, Object> handlerMap = new HashMap<>();
    //服务端口
    private static final int port = 8000;

    /**
     * 初始化
     */
    public static void init() {
        handlerMap.put(IHelloService.class.getName(), new HelloServiceImpl());
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        init();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup work = new NioEventLoopGroup(0, new DefaultThreadFactory("work"));
        try{
            serverBootstrap.group(boss, work);
            //nagle算法，将小的碎片数据连接成更大的报文来提高发送效率
            serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
            //最大等待连接数
            serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);
            //serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            //metrics
            MetricsHandler metricsHandler = new MetricsHandler();

            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                    pipeline.addLast("metricHandler", metricsHandler);

                    pipeline.addLast("frameDecoder", new RpcFrameDecoder());
                    pipeline.addLast("frameEncoder", new RpcFrameEncoder());

                    pipeline.addLast("protocolEncoder", new RpcProtocolEncoder());
                    pipeline.addLast("protocolDecoder", new RpcProtocolDecoder());

                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                    pipeline.addLast("handler", new RpcHandler());
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            LOGGER.info("服务启动.....端口：{}", port);

            channelFuture.channel().closeFuture().sync();
        } finally {
            //group.shutdownGracefully();
        }
    }

}
