package com.alita.framework.event.context;


public class EventHandlerDefinition {

    /**
     * 当前处理器所在位置
     */
    private int order = 0;

    /**
     * 处理器链定义
     */
    private String definitions = null;


    public int getOrder() {
        return order;
    }

    public EventHandlerDefinition setOrder(int order) {
        this.order = order;
        return this;
    }

    public String getDefinitions() {
        return definitions;
    }

    public EventHandlerDefinition setDefinitions(String definitions) {
        this.definitions = definitions;
        return this;
    }


    @Override
    public String toString() {
        return "EventHandlerDefinition{" +
                "order=" + order +
                ", definitions='" + definitions + '\'' +
                '}';
    }
}
