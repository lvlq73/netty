package com.llq.netty.entity;

/**
 * @author lvlianqi
 * @description 返回结果vo
 * @createDate 2020/4/27
 */
public class ResultVo {

    private ResultVo() {

    }

    public static RpcResponseBody success(Object result) {
        RpcResponseBody responseBody = new RpcResponseBody(result);
        return responseBody;
    }

    public static RpcResponseBody error(Throwable throwable) {
        RpcResponseBody responseBody = new RpcResponseBody(throwable);
        return responseBody;
    }

    public static RpcResponseBody error(String errorMsg, Throwable throwable) {
        RpcResponseBody responseBody = new RpcResponseBody(throwable);
        responseBody.setResult(errorMsg);
        return responseBody;
    }
}
