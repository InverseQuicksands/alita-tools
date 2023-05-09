package com.alita.framework.event.build;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

public interface Builder {

    /**
     * 创建 Disruptor.
     *
     * @param ringBufferSize ringbuffer 大小
     * @param threadFactory 线程工厂
     * @param waitStrategy 等待策略
     * @param producerType 是否单生产者
     * @param eventFactory 事件工厂
     * @param workHandlers 处理类实例
     * @return Disruptor
     */
    <T> Disruptor<T> build(int ringBufferSize,
                       ThreadFactory threadFactory,
                       EventFactory eventFactory,
                       ProducerType producerType,
                       WaitStrategy waitStrategy,
                       WorkHandler<T>[] workHandlers);

}
