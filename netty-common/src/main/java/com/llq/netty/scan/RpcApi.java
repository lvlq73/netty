package com.llq.netty.scan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lvlianqi
 * @description RpcApi 注解定义在服务接口类上
 * @createDate 2019/11/27 21:16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcApi {
    //别名
    String alias();

    String serviceId() default "";
}
