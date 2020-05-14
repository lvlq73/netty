package com.llq.netty.handler;

import com.llq.netty.Server;
import com.llq.netty.entity.RpcMessage;
import com.llq.netty.entity.RpcRequestBody;
import com.llq.netty.entity.RpcResponseBody;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author lvlianqi
 * @description 服务端业务处理
 * @createDate 2020/4/26
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcMessage<RpcRequestBody>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

    /**
     * 接收消息
     * @param ctx
     * @param requestMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<RpcRequestBody> requestMessage) throws Exception {
        RpcMessage responseMessage = new RpcMessage();
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

        /*FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);*/
    }
}
