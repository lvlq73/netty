package com.llq.netty.codec;

import com.llq.netty.entity.MessageBody;
import com.llq.netty.entity.RpcMessage;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author lvlianqi
 * @description 消息编码
 * @createDate 2020/4/23
 */
public class RpcProtocolEncoder extends MessageToMessageEncoder<RpcMessage<MessageBody>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage<MessageBody> requestBody, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        requestBody.encode(buffer);
        out.add(buffer);
    }
}
