package com.llq.netty.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author lvlianqi
 * @description
 * @date 2021/11/27
 */
public class RpcPropertiesUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(RpcPropertiesUtil.class);

    private static Properties PROPS;

    static {
       load("/netty-rpc.properties");
    }

    public static void load(String path) {
        PROPS = new Properties();
        InputStream in;
        try {
            in = RpcPropertiesUtil.class.getResourceAsStream(path);
            PROPS.load(in);
        } catch (IOException e) {
            LOGGER.error("读取配置文件异常", e);
        }
    }

    public static String getValue(String name) {
        return PROPS.getProperty(name);
    }

    public static <T> T getValue(String name, Class<T> cls) {
        ObjectMapper om = new ObjectMapper();
        return om.convertValue(PROPS.get(name), cls);
    }
}
