package com.alita.framework.job.core.enums;

/**
 * 注册配置
 *
 * <p>
 *  心跳时间和超时时间配置.
 *  注册类型：server or handler.
 * </p>
 */
public class RegistryConfig {

    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    public enum RegistType{
        EXECUTOR, ADMIN
    }

}
