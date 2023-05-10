package com.alita.framework.core.thread;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

/**
 * 线程池创建类.
 *
 * @see ThreadPoolExecutor
 */
public class ThreadExecutorBuilder implements Serializable {

    private static final long serialVersionUID = -8162464852966837473L;

    /** 默认的等待队列容量 */
    public static final int DEFAULT_QUEUE_CAPACITY = 2048;

    /** 初始池大小 */
    private int corePoolSize = 10;

    /** 最大池大小（允许同时执行的最大线程数） */
    private int maxPoolSize = 200;

    /** 线程存活时间，即当池中线程多于初始大小时，多出的线程保留的时长 */
    private long keepAliveTime = TimeUnit.MILLISECONDS.toMillis(1000);

    /** 队列，用于存放未执行的线程 */
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue(DEFAULT_QUEUE_CAPACITY);

    /** 线程工厂，用于自定义线程创建 */
    private ThreadFactory threadFactory = new NamedThreadFactory("", false);

    /** 当线程阻塞（block）时的异常处理器，所谓线程阻塞即线程池和等待队列已满，无法处理线程时采取的策略 */
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

    /** 线程执行超时后是否回收线程 */
    private Boolean allowCoreThreadTimeOut = true;

    /**
     * 设置初始池大小，默认10
     *
     * @param corePoolSize 初始池大小
     * @return this
     */
    public ThreadExecutorBuilder setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * 设置最大池大小（允许同时执行的最大线程数）
     *
     * @param maxPoolSize 最大池大小（允许同时执行的最大线程数）
     * @return this
     */
    public ThreadExecutorBuilder setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * 设置线程存活时间，即当池中线程多于初始大小时，多出的线程保留的时长
     *
     * @param keepAliveTime 线程存活时间
     * @param unit          单位: 毫秒
     * @return this
     */
    public ThreadExecutorBuilder setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
        return setKeepAliveTime(unit.toMillis(keepAliveTime));
    }

    /**
     * 设置线程存活时间，即当池中线程多于初始大小时，多出的线程保留的时长，单位纳秒
     *
     * @param keepAliveTime 线程存活时间，单位纳秒
     * @return this
     */
    public ThreadExecutorBuilder setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * 设置队列，用于存在未执行的线程<br>
     * 可选队列有：
     * <pre>
     * 1. {@link SynchronousQueue}  它将任务直接提交给线程而不保持它们。当运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
     * 2. {@link LinkedBlockingQueue} 默认无界队列，当运行线程大于corePoolSize时始终放入此队列，此时maxPoolSize无效。
     * 当构造LinkedBlockingQueue对象时传入参数，变为有界队列，队列满时，运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
     * 3. {@link ArrayBlockingQueue}  有界队列，相对无界队列有利于控制队列大小，队列满时，运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
     * </pre>
     *
     * @param workQueue 队列
     * @return this
     */
    public ThreadExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * 使用{@link ArrayBlockingQueue} 做为等待队列<br>
     * 有界队列，相对无界队列有利于控制队列大小，队列满时，运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
     *
     * @param capacity 队列容量
     * @return this
     */
    public ThreadExecutorBuilder useArrayBlockingQueue(int capacity) {
        return setWorkQueue(new ArrayBlockingQueue<>(capacity));
    }

    /**
     * 使用{@link SynchronousQueue} 做为等待队列（非公平策略）<br>
     * 它将任务直接提交给线程而不保持它们。当运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
     *
     * @return this
     */
    public ThreadExecutorBuilder useSynchronousQueue() {
        return useSynchronousQueue(false);
    }

    /**
     * 使用{@link SynchronousQueue} 做为等待队列<br>
     * 它将任务直接提交给线程而不保持它们。当运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
     *
     * @param fair 是否使用公平访问策略
     * @return this
     */
    public ThreadExecutorBuilder useSynchronousQueue(boolean fair) {
        return setWorkQueue(new SynchronousQueue<>(fair));
    }

    /**
     * 设置线程工厂，用于自定义线程创建
     *
     * @param threadFactory 线程工厂
     * @return this
     */
    public ThreadExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * 设置当线程阻塞（block）时的异常处理器，所谓线程阻塞即线程池和等待队列已满，无法处理线程时采取的策略
     * <p>此处可以使用JDK预定义的几种策略:
     * <pre>
     *  AbortPolicy: 处理程序遭到拒绝将抛出RejectedExecutionException.
     *  DiscardPolicy: 放弃当前任务.
     *  DiscardOldestPolicy: 如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）.
     *  CallerRunsPolicy: 由主线程来直接执行.
     * </pre>
     *
     * @param handler {@link RejectedExecutionHandler}
     * @return this
     */
    public ThreadExecutorBuilder setHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
        return this;
    }

    /**
     * 设置线程执行超时后是否回收线程
     *
     * @param allowCoreThreadTimeOut 线程执行超时后是否回收线程
     * @return this
     */
    public ThreadExecutorBuilder setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * 构建ThreadPoolExecutor
     */
    public ThreadPoolExecutor build() {
        return build(this);
    }

    /**
     * 关闭线程池
     * @param threadExecutorBuilder
     */
    public void shutdown(ThreadPoolExecutor threadExecutorBuilder) {
        threadExecutorBuilder.shutdown();
    }

    /**
     * 构建ThreadPoolExecutor
     *
     * @param threadExecutorBuilder {@link ThreadExecutorBuilder}
     * @return {@link ThreadPoolExecutor}
     */
    private static ThreadPoolExecutor build(ThreadExecutorBuilder threadExecutorBuilder) {
        final int corePoolSize = threadExecutorBuilder.corePoolSize;
        final int maxPoolSize = threadExecutorBuilder.maxPoolSize;
        final long keepAliveTime = threadExecutorBuilder.keepAliveTime;
        final BlockingQueue<Runnable> workQueue;
        if (null != threadExecutorBuilder.workQueue) {
            workQueue = threadExecutorBuilder.workQueue;
        } else {
            // corePoolSize为0则要使用SynchronousQueue避免无限阻塞
            workQueue = (corePoolSize <= 0) ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
        }
        final ThreadFactory threadFactory = (null != threadExecutorBuilder.threadFactory) ? threadExecutorBuilder.threadFactory : Executors.defaultThreadFactory();
        final RejectedExecutionHandler handler = (null != threadExecutorBuilder.handler) ? threadExecutorBuilder.handler : new ThreadPoolExecutor.AbortPolicy();

        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.NANOSECONDS,
                workQueue,
                threadFactory,
                handler
        );
        if (null != threadExecutorBuilder.allowCoreThreadTimeOut) {
            threadPoolExecutor.allowCoreThreadTimeOut(threadExecutorBuilder.allowCoreThreadTimeOut);
        }
        return threadPoolExecutor;
    }
}
