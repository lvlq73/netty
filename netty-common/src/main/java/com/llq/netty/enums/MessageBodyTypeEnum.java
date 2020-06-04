package com.llq.netty.enums;

import com.llq.netty.entity.RpcKeepalive;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;

/**
 * @author lvlianqi
 * @description 消息体类型枚举
 * @createDate 2020/6/2
 */
public enum MessageBodyTypeEnum {
    REQUEST((short)1, RpcRequestBody.class),
    KEEPALIVE((short)2, RpcKeepalive.class),
    RESPONSE((short)3, RpcResponseBody.class);

    private short value;
    private Class<?> tClass;

    MessageBodyTypeEnum(short value, Class<?> tClass) {
        this.value = value;
        this.tClass = tClass;
    }

    public short value() {
        return value;
    }

    public Class<?> tClass() {
        return tClass;
    }

    public Class<?> getTClass(short value) {
        for (MessageBodyTypeEnum item : MessageBodyTypeEnum.values()) {
            if (item.value == value) {
                return item.tClass();
            }
        }
        return null;
    }
}
