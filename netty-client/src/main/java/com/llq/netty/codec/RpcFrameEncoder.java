package com.llq.netty.codec;


import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author lvlianqi
 * @description 编码 防止粘包半包
 * @createDate 2020/4/23
 */
public class RpcFrameEncoder extends LengthFieldPrepender {
    public RpcFrameEncoder() {
        super(2);
    }
}
