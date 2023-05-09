package com.alita.framework.event.context.waitstrategy;

import com.lmax.disruptor.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Disruptor 策略.
 *  <p>消费者等待{@link SequenceBarrier}时，不同的等待策略在延迟和CPU资源的占用上不同。
 *
 * @author liu sha
 * @since 1.0
 * @see BlockingWaitStrategy
 * @see SleepingWaitStrategy
 * @see YieldingWaitStrategy
 * @see BusySpinWaitStrategy
 * @see LiteBlockingWaitStrategy
 * @see LiteTimeoutBlockingWaitStrategy
 * @see TimeoutBlockingWaitStrategy
 * @see PhasedBackoffWaitStrategy
 */
public class WaitStrategys {

    /**
     * disruptor 默认策略.
     *
     * <p>BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现.
     * 内部维护了一个重入锁{@link ReentrantLock}和{@link Condition}.
     */
    public final static WaitStrategy blockingWaitStrategy = new BlockingWaitStrategy();

    /**
     * 三段式等待策略.
     *
     * <p>第一阶段自旋，第二阶段执行{@link Thread#yield()}交出CPU，第三阶段睡眠执行时间，反复的睡眠.
     *
     * <p>SleepingWaitStrategy 的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，
     * 但其对生产者线程的影响最小，算是CPU与性能之间的一个折中，当CPU资源紧张时可以考虑使用该策略。
     * 适合用于异步日志类似的场景.
     */
    public final static WaitStrategy sleepingWaitStrategy = new SleepingWaitStrategy();

    /**
     * YieldingWaitStrategy 是可以被用在低延迟系统中的两个策略之一，这种策略在减低系统延迟的同时也会增加CPU运算量.
     *
     * <p>YieldingWaitStrategy 策略会循环等待 sequence 增加到合适的值。循环中调用{@link Thread#yield()}允许其他准备好的线程执行.
     * 如果需要高性能而且事件消费者线程比逻辑内核少的时候，推荐使用YieldingWaitStrategy策略。例如：在开启超线程的时候.
     *
     * <p>该策略在尝试一定次数的自旋等待(空循环)之后使用尝试让出cpu。
     * 该策略将会占用大量的CPU资源(100%)，但是比{@link BusySpinWaitStrategy}策略更容易在其他线程需要CPU时让出CPU。
     * 它有着较低的延迟、较高的吞吐量，以及较高CPU占用率。当CPU数量足够时，可以使用该策略。
     */
    public final static WaitStrategy yieldingWaitStrategy = new YieldingWaitStrategy();

    /**
     * BusySpinWaitStrategy是性能最高的等待策略，同时也是对部署环境要求最高的策略.
     *
     * <p>这个性能最好用在事件处理线程比物理内核数目还要小的时候。例如：在禁用超线程技术的时候.
     *
     * <p>该策略使用自旋(空循环)来在barrier上等待.通过占用CPU资源去比避免系统调用带来的延迟抖动。
     * 最好在线程能绑定到特定的CPU核心时使用,(会持续占用CPU资源，基本不会让出CPU资源).
     *
     * <p>特征：极低的延迟，极高的吞吐量，以及极高的CPU占用.
     * 如果你要使用该等待策略，确保有足够的CPU资源，且你能接受它带来的CPU使用率.
     *
     * <p>警告：JDK9 之下慎用.
     */
    public final static WaitStrategy busySpinWaitStrategy = new BusySpinWaitStrategy();

    /**
     * 轻量级的带超时的阻塞等待策略.
     *
     * <p>如果生产者生产速率不够，则阻塞式等待生产者一段时间。
     * 如果是等待依赖的其它消费者，则轮询式等待。
     *
     * <p>警告：与{@link TimeoutBlockingWaitStrategy}不同，消费者可能在有可消费者的事件时仍然处理阻塞状态，
     * 因此使用该策略时超时时间不可以太长。如果不需要精确的等待通知策略，
     * 该策略可能在多数情况下都优于{@link TimeoutBlockingWaitStrategy}。
     */
    public final static WaitStrategy liteTimeoutBlockingWaitStrategy = new LiteTimeoutBlockingWaitStrategy(100, TimeUnit.MILLISECONDS);

    /**
     * 分阶段性的等待策略，在不同的等阶阶段采用不同的方式等待。
     *
     * <p>四段式：第一阶段自旋指定次数，第二阶段自旋指定时间，第三阶段执行{@link Thread#yield()}交出CPU，第四阶段调用成员变量的waitFor方法，
     * 这个成员变量可以设置为：BlockingWaitStrategy、LiteBlockingWaitStrategy、SleepingWaitStrategy 这三个中的一个.
     */
    public final static WaitStrategy phasedBackoffWaitStrategy = new PhasedBackoffWaitStrategy(10, 100, TimeUnit.MILLISECONDS, blockingWaitStrategy);

    /**
     * 如果生产者生产速率不够，则阻塞式等待生产者一段时间。
     * 如果是等待依赖的其它消费者，则轮询式等待。
     */
    public final static WaitStrategy timeoutBlockingWaitStrategy = new TimeoutBlockingWaitStrategy(10, TimeUnit.MILLISECONDS);

    /**
     * 基于{@link BlockingWaitStrategy}，在没有锁竞争的时候会省去唤醒操作。
     * 因测试不充分，不建议使用.
     */
    public final static WaitStrategy liteBlockingWaitStrategy = new LiteBlockingWaitStrategy();

}
