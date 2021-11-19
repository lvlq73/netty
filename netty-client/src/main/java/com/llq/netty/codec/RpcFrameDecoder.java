package com.llq.netty.codec;


import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author lvlianqi
 * @description 解码 防止粘包半包
 * @createDate 2020/4/23
 */
public class RpcFrameDecoder extends LengthFieldBasedFrameDecoder {
    public RpcFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
