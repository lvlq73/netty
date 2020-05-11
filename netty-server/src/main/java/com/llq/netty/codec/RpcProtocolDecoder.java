package com.llq.netty.codec;

import com.llq.netty.entity.RpcMessage;
import com.llq.netty.entity.RpcRequestBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author lvlianqi
 * @description 消息解码
 * @createDate 2020/4/23
 */
public class RpcProtocolDecoder extends MessageToMessageDecoder<ByteBuf>  {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        RpcMessage<RpcRequestBody> requestBody = new RpcMessage<>();
        requestBody.decode(byteBuf, RpcRequestBody.class);
        out.add(requestBody);
    }
}
