package com.llq.netty.entity;

/**
 * @author lvlianqi
 * @description 消息头
 * @createDate 2020/4/23
 */
public class MessageHeader {

    private int version = 1;
    //唯一id(来判断是否为同一个请求)
    private long streamId;

    public MessageHeader() {
    }

    public MessageHeader(long streamId) {
        this.streamId = streamId;
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
}
