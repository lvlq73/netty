package com.llq.netty.scan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lvlianqi
 * @description RpcService 注解定义在服务接口的实现类上，需要对该实现类指定远程接口
 * @createDate 2019/11/27 21:16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    Class<?> value();
}
