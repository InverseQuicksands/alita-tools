package com.alita.framework.event.context;

import org.springframework.beans.factory.DisposableBean;

import java.io.Closeable;

/**
 * Disruptor 上下文抽象接口.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public interface DisruptorContext extends DisruptorEventPublisher, Lifecycle,
        DisposableBean, Closeable {

    /**
     * 返回生命周期接口实例.
     *
     * @return 返回生命周期接口实例.
     */
    Lifecycle getLifecycle();

    /**
     * 返回事件发布实例.
     *
     * @return 返回事件发布实例.
     */
    DisruptorEventPublisher getDisruptorEventPublisher();

    /**
     * 初始化上下文.
     */
    void init();
}
