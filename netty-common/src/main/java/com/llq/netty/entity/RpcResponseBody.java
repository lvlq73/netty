package com.llq.netty.entity;

/**
 * @author lvlianqi
 * @description 封装 RPC 响应实体类
 * @createDate 2020/4/23
 */
public class RpcResponseBody extends MessageBody {
    /**
     * 错误信息
     */
    private Throwable error;
    /**
     * 返回的内容
     */
    private Object result;

    public RpcResponseBody(Object result) {
        this.result = result;
    }

    public RpcResponseBody(Throwable error) {
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


}
