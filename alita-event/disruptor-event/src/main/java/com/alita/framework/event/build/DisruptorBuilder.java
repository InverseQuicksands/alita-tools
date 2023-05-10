package com.alita.framework.event.build;

import com.alita.framework.core.thread.NamedThreadFactory;
import com.alita.framework.event.context.waitstrategy.WaitStrategys;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

public class DisruptorBuilder<T> implements Builder {


    private int ringBufferSize = 1024;
    private ProducerType producerType = ProducerType.SINGLE;
    private ThreadFactory threadFactory = new NamedThreadFactory("Event", false);
    private WaitStrategy waitStrategy = WaitStrategys.blockingWaitStrategy;


    public DisruptorBuilder() {

    }

    public DisruptorBuilder(int ringBufferSize, ProducerType producerType,
                            ThreadFactory threadFactory, WaitStrategy waitStrategy) {

        this.ringBufferSize = ringBufferSize;
        this.producerType = producerType;
        this.threadFactory = threadFactory;
        this.waitStrategy = waitStrategy;
    }

    public Disruptor<T> build(EventFactory eventFactory, WorkHandler<T>[] workHandlers) {

        return build(this.ringBufferSize, this.threadFactory, eventFactory,
                this.producerType, waitStrategy, workHandlers);
    }


    /**
     * 创建单生产者的 Disruptor.
     *
     * @param ringBufferSize ringbuffer 大小
     * @param threadFactory  线程工厂
     * @param waitStrategy   等待策略
     * @param eventFactory   事件工厂
     * @param workHandlers   处理类实例
     * @return Disruptor
     */
    @Override
    public <T> Disruptor<T> build(int ringBufferSize,
                              ThreadFactory threadFactory,
                              EventFactory eventFactory,
                              ProducerType producerType,
                              WaitStrategy waitStrategy,
                              WorkHandler<T>[] workHandlers) {

        Disruptor<T> disruptor = new Disruptor<T>(
                eventFactory,
                ringBufferSize, threadFactory,
                producerType, waitStrategy);

        disruptor.handleEventsWithWorkerPool(workHandlers);

        return disruptor;
    }

}
