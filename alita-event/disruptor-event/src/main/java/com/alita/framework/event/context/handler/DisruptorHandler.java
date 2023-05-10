package com.alita.framework.event.context.handler;


import com.alita.framework.event.context.event.DisruptorBindEvent;

/**
 * disruptor事件处理器.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public interface DisruptorHandler<T extends DisruptorBindEvent> {

    void doHandler(T event) throws Exception;
}
