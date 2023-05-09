package com.alita.framework.event.context.event;

import java.util.EventObject;

/**
 * disruptor 基本事件，作为所有disruptor事件的父类.
 *
 * <p>任何事件类都必须继承JDK的{@link EventObject}.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @see EventObject
 */
public class DisruptorEvent extends EventObject {

    /** Event Name */
    private String event;

    /** Event Tag */
    private String tag;

    /** Event Keys */
    private String key;

    /** System time when the event happened */
    private long timestamp;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DisruptorEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 返回表达式.
     */
    public String getRouteExpression() {

        return new StringBuilder("/")
                .append(getEvent())
                .append("/")
                .append(getTag())
                .append("/")
                .append(getKey())
                .toString();

    }


    /**
     * Return the system time in milliseconds when the event happened.
     * @return system time in milliseconds
     */
    public final long getTimestamp() {
        return this.timestamp;
    }


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
