package com.alita.framework.event.template;

import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DisruptorTemplate {

    @Autowired
    @Qualifier("disruptorProducer")
    protected Disruptor<DisruptorBindEvent> disruptor;

    @Autowired
    protected EventTranslatorOneArg<DisruptorBindEvent, DisruptorBindEvent> oneArgEventTranslator;

    public void publishEvent(DisruptorBindEvent event) {
        disruptor.publishEvent(oneArgEventTranslator, event);
    }

}
