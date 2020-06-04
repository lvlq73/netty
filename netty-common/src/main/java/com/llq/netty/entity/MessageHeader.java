package com.llq.netty.entity;

import com.llq.netty.enums.MessageBodyTypeEnum;

/**
 * @author lvlianqi
 * @description 消息头
 * @createDate 2020/4/23
 */
public class MessageHeader {

    private int version = 1;
    //唯一id(来判断是否为同一个请求)
    private long streamId;
    //body类型
    private short type;

    public MessageHeader() {
    }

    public MessageHeader(long streamId) {
        this.streamId = streamId;
    }

    public MessageHeader(long streamId, MessageBodyTypeEnum messageBodyTypeEnum) {
        this.streamId = streamId;
        this.type = messageBodyTypeEnum.value();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getStreamId() {
        return streamId;
    }

    public void setStreamId(long streamId) {
        this.streamId = streamId;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }
}
