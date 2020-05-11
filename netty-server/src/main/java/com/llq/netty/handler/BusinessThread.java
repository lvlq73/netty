package com.llq.netty.handler;

import com.llq.netty.Server;
import com.llq.netty.entity.RpcMessage;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author lvlianqi
 * @description
 * @createDate 2020/5/8
 */
public class BusinessThread implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessThread.class);

    private RpcMessage<RpcRequestBody> requestMessage;
    private ChannelHandlerContext ctx;

    public BusinessThread(ChannelHandlerContext ctx, RpcMessage<RpcRequestBody> requestMessage){
        this.ctx = ctx;
        this.requestMessage = requestMessage;
    }

    @Override
    public void run() {
        RpcMessage<RpcResponseBody> responseMessage = new RpcMessage();
        RpcRequestBody requestBody = requestMessage.getMessageBody();
        LOGGER.info("服务端接收消息，streamId:{}", requestMessage.getMessageHeader().getStreamId());
        try {
            Object result = handle(requestBody);
            responseMessage.setMessageBody(new RpcResponseBody(result));
        } catch (Throwable t) {
            responseMessage.setMessageBody(new RpcResponseBody(t));
        }
        responseMessage.setMessageHeader(requestMessage.getMessageHeader());
        ctx.writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE);
        LOGGER.info("服务端处理消息完毕，streamId:{}", requestMessage.getMessageHeader().getStreamId());
    }

    /**
     * 业务处理
     * @param requestBody
     * @return Object
     * @throws Throwable
     */
    private Object handle(RpcRequestBody requestBody) throws Throwable {
        String className = requestBody.getClassName();
        Object serviceBean = Server.handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = requestBody.getMethodName();
        Class<?>[] parameterTypes = requestBody.getParameterTypes();
        Object[] parameters = requestBody.getParameters();

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }
}
