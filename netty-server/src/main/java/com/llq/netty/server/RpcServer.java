package com.llq.netty.server;

import com.llq.netty.codec.RpcFrameDecoder;
import com.llq.netty.codec.RpcFrameEncoder;
import com.llq.netty.codec.RpcProtocolDecoder;
import com.llq.netty.codec.RpcProtocolEncoder;
import com.llq.netty.handler.RpcHandler;
import com.llq.netty.handler.ServerIdleCheckHandler;
import com.llq.netty.scan.ServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvlianqi
 * @description RpcServer 服务器
 * @createDate 2019/11/27 21:53
 */
public class RpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String host;
    private int port;
    //private ServiceRegistry serviceRegistry;
    private NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
    private NioEventLoopGroup work = new NioEventLoopGroup(0, new DefaultThreadFactory("work"));

    public RpcServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));

        try{
            serverBootstrap.group(boss, work);
            //nagle算法，将小的碎片数据连接成更大的报文来提高发送效率
            serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
            //最大等待连接数
            serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);
            //serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            //metrics
            //MetricsHandler metricsHandler = new MetricsHandler();
            //业务线程池
            UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));

            //流量整形,看情况设置
            //GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(new NioEventLoopGroup(), 100 * 1024 * 1024, 100 * 1024 * 1024);

            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                    //pipeline.addLast("TShandler", globalTrafficShapingHandler);
                    //监控指标
                    //pipeline.addLast("metricHandler", metricsHandler);
                    //服务端idle检测
                    pipeline.addLast("idleHandler", new ServerIdleCheckHandler());

                    pipeline.addLast("frameDecoder", new RpcFrameDecoder());
                    pipeline.addLast("frameEncoder", new RpcFrameEncoder());

                    //TODO 添加压缩解压缩处理

                    pipeline.addLast("protocolEncoder", new RpcProtocolEncoder());
                    pipeline.addLast("protocolDecoder", new RpcProtocolDecoder());

                    //减少flush的次数，牺牲延迟增加吞吐量，参数设置5为读取5次进行一次flush, consolidateWhenNoReadInProgress为true为异步情况下的优化flush
                    //FlushConsolidationHandler里的readInProgress参数
                    //同步 ：read -> writeAndFlush -> readComplete  readInProgress为true发生在read 到 readComplete之间
                    //异步 : read -> readComplete -> writeAndFlush（因为开启异步所以可能发生在readComplete之后） readInProgress为true发生在read 到 readComplete之间
                    pipeline.addLast("flushEnhance", new FlushConsolidationHandler(5, true));

                    //pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                    pipeline.addLast(business, "handler", new RpcHandler());
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();

            LOGGER.info("服务启动.....端口：{}", port);

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("启动异常", e);
        }finally {
            //group.shutdownGracefully();
        }
    }

    public void close() {
        work.shutdownGracefully();
        boss.shutdownGracefully();
    }
}
