package com.alita.framework.id.tinyid.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程创建工厂类，此工厂可选配置：
 * <pre>
 * 1. 自定义线程命名前缀
 * 2. 自定义是否守护线程
 * </pre>
 */
public class NamedThreadFactory implements ThreadFactory {

    /** 命名前缀 */
    private final String prefix;
    /** 线程组 */
    private final ThreadGroup threadGroup;
    /** 线程组 */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /** 是否守护线程 */
    private final boolean isDaemon;
    /** 无法捕获的异常统一处理 */
    private final Thread.UncaughtExceptionHandler handler;


    /**
     * 构造方法
     *
     * @param prefix 线程名前缀
     * @param isDaemon 是否守护线程
     */
    public NamedThreadFactory(String prefix, boolean isDaemon) {
        this(prefix, null, isDaemon);
    }

    /**
     * 构造方法
     *
     * @param prefix 线程名前缀
     * @param threadGroup 线程组，可以为null
     * @param isDaemon 是否守护线程
     */
    public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon) {
        this(prefix, threadGroup, isDaemon, null);
    }

    /**
     * 构造方法
     *
     * @param prefix 线程名前缀
     * @param threadGroup 线程组，可以为null
     * @param isDaemon 是否守护线程
     * @param handler 未捕获异常处理
     */
    public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon,
                              Thread.UncaughtExceptionHandler handler) {

        this.prefix = prefix + "-";
        this.isDaemon = isDaemon;
        this.handler = handler;
        if (null == threadGroup) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                threadGroup = securityManager.getThreadGroup();
            } else {
                threadGroup = Thread.currentThread().getThreadGroup();
            }
        }
        this.threadGroup = threadGroup;
    }

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param runnable a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     * create a thread is rejected
     */
    @Override
    public Thread newThread(Runnable runnable) {
        final Thread thread = new Thread(this.threadGroup, runnable,
                prefix + threadNumber.getAndIncrement(), 0);

        thread.setUncaughtExceptionHandler(this.handler);
        if (this.isDaemon && (!thread.isDaemon())) {
            thread.setDaemon(this.isDaemon);
        }
        // 标准优先级
        if (Thread.NORM_PRIORITY != thread.getPriority()) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
