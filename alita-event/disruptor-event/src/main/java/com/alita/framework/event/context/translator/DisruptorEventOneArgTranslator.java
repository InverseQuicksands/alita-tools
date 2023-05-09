package com.alita.framework.event.context.translator;

import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.lmax.disruptor.EventTranslatorOneArg;

public class DisruptorEventOneArgTranslator implements EventTranslatorOneArg<DisruptorBindEvent, DisruptorBindEvent> {

    @Override
    public void translateTo(DisruptorBindEvent bindEvent, long sequence, DisruptorBindEvent dataEvent) {
        bindEvent.setEvent(dataEvent.getEvent());
        bindEvent.setTag(dataEvent.getTag());
        bindEvent.setData(dataEvent.getData());
    }

}

