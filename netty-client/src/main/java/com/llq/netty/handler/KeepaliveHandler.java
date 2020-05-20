package com.llq.netty.handler;

import com.llq.netty.entity.RpcKeepalive;
import com.llq.netty.entity.RpcMessage;
import com.llq.netty.utils.IdUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class KeepaliveHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeepaliveHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            LOGGER.info("write idle happen. so need to send keepalive to keep connection not closed by server");
            RpcMessage requestMessage = new RpcMessage<RpcKeepalive>(IdUtil.nextId(), new RpcKeepalive());
            ctx.writeAndFlush(requestMessage);
        }
        super.userEventTriggered(ctx, evt);
    }
}
