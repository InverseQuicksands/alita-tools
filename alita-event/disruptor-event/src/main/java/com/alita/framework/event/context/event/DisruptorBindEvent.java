package com.alita.framework.event.context.event;

/**
 * DisruptorBindEvent
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-05-06 11:20:22
 */
public class DisruptorBindEvent<T> extends DisruptorEvent {

    private T data;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DisruptorBindEvent(Object source) {
        super(source);
    }

    /**
     * 返回数据对象.
     * @return 数据对象.
     */
    public T getData() {
        return data;
    }

    public DisruptorBindEvent<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "DisruptorBindEvent{" +
                "data=" + data +
                '}' + super.toString();
    }
}
