package com.alita.framework.event.context;

import com.alita.framework.event.context.event.DisruptorBindEvent;

/**
 * 数据事件发布接口.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
@FunctionalInterface
public interface DisruptorEventPublisher {

    /**
     * 发布事件.
     *
     * @param event 数据事件.
     */
    void publishEvent(DisruptorBindEvent event) throws Exception;
}
