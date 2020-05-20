package com.llq.netty.handler;

import io.netty.handler.timeout.IdleStateHandler;
/**
 * @author lvlianqi
 * @description 客户端idle检测
 * @createDate 2020/5/20
 */
public class ClientIdleCheckHandler extends IdleStateHandler {

    public ClientIdleCheckHandler() {
        super(0, 6, 0);
    }

}
