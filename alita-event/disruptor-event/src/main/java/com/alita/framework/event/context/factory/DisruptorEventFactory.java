package com.alita.framework.event.context.factory;

import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.lmax.disruptor.EventFactory;

/**
 * DisruptorEventFactory
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-05-06 11:23:35
 */
public class DisruptorEventFactory implements EventFactory<DisruptorBindEvent> {

    @Override
    public DisruptorBindEvent newInstance() {
        return new DisruptorBindEvent(this);
    }
}
