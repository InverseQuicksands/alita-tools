package com.alita.framework.event.context.support;

import com.alita.framework.event.context.Lifecycle;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * disruptor生命周期默认处理器.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public class DefaultLifecycleProcessor implements Lifecycle {

    private volatile AtomicBoolean running = new AtomicBoolean(false);

    private Disruptor disruptor;

    public DefaultLifecycleProcessor setDisruptor(Disruptor disruptor) {
        this.disruptor = disruptor;
        return this;
    }

    /**
     * disruptor 启动
     */
    @Override
    public void start() {
        if (!running.get()) {
            this.disruptor.start();
        }
        this.running.compareAndSet(false, true);
    }

    /**
     * disruptor 停止
     */
    @Override
    public void stop() {
        if (running.get()) {
            this.disruptor.shutdown();
        }
        this.running.compareAndSet(true, false);
    }

    /**
     * 是否已经启动.
     *
     * @return 是否已经启动.
     */
    @Override
    public boolean isRunning() {
        return this.running.get();
    }
}
