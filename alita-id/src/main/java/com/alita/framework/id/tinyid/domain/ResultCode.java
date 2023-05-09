package com.alita.framework.id.tinyid.domain;

/**
 * ResultCode 枚举类.
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 13:02
 **/
public class ResultCode {

    /**
     * 正常可用
     */
    public static final int NORMAL = 1;

    /**
     * 需要去加载nextId
     */
    public static final int LOADING = 2;

    /**
     * 超过maxId 不可用
     */
    public static final int OVER = 3;
}
