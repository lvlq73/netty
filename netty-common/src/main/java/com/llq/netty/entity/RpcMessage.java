package com.llq.netty.entity;

import com.llq.netty.enums.MessageBodyTypeEnum;
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

    public RpcMessage(String streamId, T messageBody) {
        this.messageHeader = new MessageHeader(streamId, MessageBodyTypeEnum.REQUEST);
        this.messageBody = messageBody;
    }

    public RpcMessage(String streamId, T messageBody, MessageBodyTypeEnum messageBodyTypeEnum) {
        this.messageHeader = new MessageHeader(streamId, messageBodyTypeEnum);
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
        byteBuf.writeLong(messageHeader.getVersion());
//        byteBuf.writeLong(messageHeader.getStreamId());
        byteBuf.writeShort(messageHeader.getType());
        String streamId = messageHeader.getStreamId();
        byteBuf.writeInt(streamId.length());
        byteBuf.writeBytes(streamId.getBytes());
        byteBuf.writeBytes(ProtostuffUtil.serialize(messageBody));
    }
    public void decode(ByteBuf msg) {
        long version = msg.readLong();
//        long streamId = msg.readLong();
        short type = msg.readShort();
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setVersion(version);
//        messageHeader.setStreamId(streamId);
        messageHeader.setType(type);
        this.messageHeader = messageHeader;

        byte[] idByte = new byte[msg.readInt()];
        msg.readBytes(idByte);
        messageHeader.setStreamId(new String(idByte));

        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        T body = (T) ProtostuffUtil.deserialize(data, MessageBodyTypeEnum.REQUEST.getTClass(type));
        this.messageBody = body;
    }
    /*public void decode(ByteBuf msg, Class<T> genericClass) {
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
    }*/
}
