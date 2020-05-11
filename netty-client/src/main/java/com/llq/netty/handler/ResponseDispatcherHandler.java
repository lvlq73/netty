package com.llq.netty.handler;

import com.llq.netty.entity.RpcMessage;
import com.llq.netty.entity.RpcResponseBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author lvlianqi
 * @description 客户端接收消息
 * @createDate 2020/4/26
 */
public class ResponseDispatcherHandler extends SimpleChannelInboundHandler<RpcMessage<RpcResponseBody>> {

    private RequestPendingCenter requestPendingCenter;

    public ResponseDispatcherHandler(RequestPendingCenter requestPendingCenter) {
        this.requestPendingCenter = requestPendingCenter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<RpcResponseBody> response) throws Exception {
        requestPendingCenter.set(response.getMessageHeader().getStreamId(), response.getMessageBody());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
