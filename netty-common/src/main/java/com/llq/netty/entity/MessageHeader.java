package com.llq.netty.entity;

import com.llq.netty.enums.MessageBodyTypeEnum;

/**
 * @author lvlianqi
 * @description 消息头
 * @createDate 2020/4/23
 */
public class MessageHeader {

    private long version = 1;
    //唯一id(来判断是否为同一个请求)
    private String streamId;
    //body类型
    private short type;

    public MessageHeader() {
    }

    public MessageHeader(String streamId) {
        this.streamId = streamId;
    }

    public MessageHeader(String streamId, MessageBodyTypeEnum messageBodyTypeEnum) {
        this.streamId = streamId;
        this.type = messageBodyTypeEnum.value();
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }


    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }
}
