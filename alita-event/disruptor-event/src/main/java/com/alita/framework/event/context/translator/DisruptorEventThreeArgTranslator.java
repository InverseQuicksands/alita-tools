package com.alita.framework.event.context.translator;

import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.lmax.disruptor.EventTranslatorThreeArg;

public class DisruptorEventThreeArgTranslator implements EventTranslatorThreeArg<DisruptorBindEvent, String, String, String> {

    @Override
    public void translateTo(DisruptorBindEvent dataEvent, long sequence, String event, String tag, String key) {
        dataEvent.setEvent(event);
        dataEvent.setTag(tag);
        dataEvent.setKey(key);
    }

}

