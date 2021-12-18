package com.llq.netty.discovery;

/**
 * @author lvlianqi
 * @description 服务地址
 * @createDate 2020/5/21
 */
@Deprecated
public class Address {
    public String host;

    public int port;

    public Address(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
