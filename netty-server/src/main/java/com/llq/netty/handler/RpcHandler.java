package com.llq.netty.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.llq.netty.entity.*;
import com.llq.netty.enums.MessageBodyTypeEnum;
import com.llq.netty.factory.ServiceFactory;
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
public class RpcHandler extends SimpleChannelInboundHandler<RpcMessage<MessageBody>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

    private static final ObjectMapper OM = new ObjectMapper();

    /**
     * 接收消息
     * @param ctx
     * @param requestMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<MessageBody> requestMessage) throws Exception {
        RpcMessage responseMessage = new RpcMessage();
        MessageBody requestBody = requestMessage.getMessageBody();
        LOGGER.info("服务端接收消息，streamId:{}", requestMessage.getMessageHeader().getStreamId());
        if (requestBody instanceof RpcRequestBody) {
            try {
                Object result = handle((RpcRequestBody) requestBody);
                responseMessage.setMessageBody(new RpcResponseBody(result));
            } catch (Throwable t) {
                responseMessage.setMessageBody(new RpcResponseBody(t));
            }
        }
        MessageHeader messageHeader = requestMessage.getMessageHeader();
        messageHeader.setType(MessageBodyTypeEnum.RESPONSE.value());
        responseMessage.setMessageHeader(messageHeader);
        //防止oom内存溢出
        if(ctx.channel().isActive() && ctx.channel().isWritable()) {
            //ctx.writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE);
            ctx.writeAndFlush(responseMessage);
            LOGGER.info("服务端处理消息完毕，streamId:{}", requestMessage.getMessageHeader().getStreamId());
        } else {
            //数据丢失，或者可以做其他处理
            LOGGER.error("message dropped");
        }
    }

    /**
     * 业务处理
     * @param requestBody
     * @return Object
     * @throws Throwable
     */
    private Object handle(RpcRequestBody requestBody) throws Throwable {
        String className = requestBody.getClassName();
        Object serviceBean = ServiceFactory.getBean(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = requestBody.getMethodName();
        Class<?>[] parameterTypes = requestBody.getParameterTypes();
        Object[] parameters = requestBody.getParameters();

        for (int i = 0; i < parameterTypes.length; i++) {
            if ("java.lang.Class".equals(parameterTypes[i].getName())) {
                try {
                    parameters[i] = Class.forName((String) parameters[i]);
                } catch (ClassNotFoundException e) {
                    parameters[i] = Object.class;
                }
            } else {
                parameters[i] = OM.convertValue(parameters[i], parameterTypes[i]);
            }
        }

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);

        /*FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);*/
    }
}
