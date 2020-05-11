package com.llq.netty.entity;

import com.llq.netty.utils.ProtostuffUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author lvlianqi
 * @description 消息实体
 * @createDate 2020/4/23
 */
public class RpcMessage<T extends MessageBody> {
    private MessageHeader messageHeader;
    private T messageBody;

    public RpcMessage() {}

    public RpcMessage(long streamId, T messageBody) {
        this.messageHeader = new MessageHeader(streamId);
        this.messageBody = messageBody;
    }

    public void setMessageHeader(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }

    public void setMessageBody(T messageBody) {
        this.messageBody = messageBody;
    }

    public MessageHeader getMessageHeader() {
        return messageHeader;
    }
    public T getMessageBody() {
        return messageBody;
    }

    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(messageHeader.getVersion());
        byteBuf.writeLong(messageHeader.getStreamId());
        byteBuf.writeBytes(ProtostuffUtil.serialize(messageBody));
    }

    public void decode(ByteBuf msg, Class<T> genericClass) {
        int version = msg.readInt();
        long streamId = msg.readLong();

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setVersion(version);
        messageHeader.setStreamId(streamId);
        this.messageHeader = messageHeader;
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        T body = (T) ProtostuffUtil.deserialize(data, genericClass);
        this.messageBody = body;
    }
}
