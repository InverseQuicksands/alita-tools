package com.alita.framework.event.context.translator;


import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.lmax.disruptor.EventTranslatorTwoArg;

public class DisruptorEventTwoArgTranslator implements EventTranslatorTwoArg<DisruptorBindEvent, String, String> {

    @Override
    public void translateTo(DisruptorBindEvent dataEvent, long sequence, String event, String tag) {
        dataEvent.setEvent(event);
        dataEvent.setTag(tag);
        dataEvent.setKey(String.valueOf(sequence));
    }

}

