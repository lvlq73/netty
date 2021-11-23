package com.llq.netty.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvlianqi
 * @description
 * @date 2021/11/23
 */
public class Usual {

    private final static Logger LOGGER = LoggerFactory.getLogger(Usual.class);

    private Usual() {}

    /**
     * Object对象转成String
     *
     * @param obj
     * @return 为null时返回""
     */
    public static String getString(Object obj)
    {
        return getString(obj, "");
    }
    /**
     * Object对象转成String
     *
     * @param obj
     * @param defaultValue 为null时返回的默认值
     * @return
     */
    public static String getString(Object obj, String defaultValue)
    {
        try
        {
            return obj == null ? defaultValue : obj.toString();
        }
        catch (Exception exp)
        {
            LOGGER.error("[" + obj + "]转成字符串失败", exp);
            return "";
        }
    }

    /**
     * Object对象转成Long
     *
     * @param obj
     * @return 为null时返回0
     */
    public static long getLong(Object obj)
    {
        return getLong(obj, 0);
    }
    /**
     * Object对象转成Long
     *
     * @param obj
     * @param defaultValue 为null时返回的默认值
     * @return
     */
    public static long getLong(Object obj, long defaultValue)
    {
        try
        {
            return obj == null || isNullOrEmpty(obj.toString()) ? 0 : Long.valueOf(obj.toString());
        }
        catch (NumberFormatException exp)
        {
            LOGGER.error("[" + obj + "]转成Long失败", exp);
            return 0;
        }
    }

    public static Boolean isNullOrEmpty(String instr)
    {
        if (instr == null || instr.length() == 0)
        {
            return true;
        }
        return false;
    }
}
