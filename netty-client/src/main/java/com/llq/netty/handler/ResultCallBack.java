package com.llq.netty.handler;

import com.llq.netty.entity.RpcResponseBody;

/**
 * @author lvlianqi
 * @description
 * @createDate 2020/4/28
 */
@Deprecated
public class ResultCallBack implements IResultCallBack<RpcResponseBody, RpcResponseBody>
{

    private RpcResponseBody responseBody;

    @Override
    public void setData(RpcResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public RpcResponseBody getResult() {
        return responseBody;
    }


}
