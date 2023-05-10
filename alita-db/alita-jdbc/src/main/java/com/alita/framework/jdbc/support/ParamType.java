package com.alita.framework.jdbc.support;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <br>
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 11:28
 **/
public class ParamType {


    /**
     * 判断是否是基础类型。
     *
     * @param parameter
     * @return
     */
    public static boolean isPassiveType(Object parameter) {
        if (parameter instanceof Integer) {
            return true;
        } else if (parameter instanceof Boolean) {
            return true;
        } else if (parameter instanceof Character) {
            return true;
        } else if (parameter instanceof Long) {
            return true;
        } else if (parameter instanceof Byte) {
            return true;
        } else if (parameter instanceof Double) {
            return true;
        } else if (parameter instanceof Float) {
            return true;
        } else if (parameter instanceof Short) {
            return true;
        } else if (parameter instanceof String) {
            return true;
        } else if (parameter instanceof BigDecimal) {
            return true;
        } else if (parameter instanceof boolean[]) {
            return true;
        } else if (parameter instanceof byte[]) {
            return true;
        } else if (parameter instanceof short[]) {
            return true;
        } else if (parameter instanceof int[]) {
            return true;
        } else if (parameter instanceof long[]) {
            return true;
        } else if (parameter instanceof float[]) {
            return true;
        } else if (parameter instanceof double[]) {
            return true;
        } else if (parameter instanceof char[]) {
            return true;
        } else if (parameter instanceof Object[]) {
            return true;
        } else if (parameter instanceof String[]) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMap(Object parameter) {
        if (parameter instanceof Map) {
            return true;
        }
        return false;
    }

}
