package com.alita.framework.event.context.handler;

import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;

/**
 * 异步事件分发处理类.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public class AsyncListenerDispatcher implements EventHandler<DisruptorBindEvent> {


    private PathMatchingHandlerChainResolver pathMatchingHandlerChainResolver;


    public AsyncListenerDispatcher(PathMatchingHandlerChainResolver pathMatchingHandlerChainResolver) {
        this.pathMatchingHandlerChainResolver = pathMatchingHandlerChainResolver;
    }

    /**
     * Called when a publisher has published an event to the {@link RingBuffer}.  The {@link BatchEventProcessor} will
     * read messages from the {@link RingBuffer} in batches, where a batch is all of the events available to be
     * processed without having to wait for any new event to arrive.  This can be useful for event handlers that need
     * to do slower operations like I/O as they can group together the data from multiple events into a single
     * operation.  Implementations should ensure that the operation is always performed when endOfBatch is true as
     * the time between that message an the next one is inderminate.
     *
     * @param dataEvent      published to the {@link RingBuffer}
     * @param sequence   of the event being processed
     * @param endOfBatch flag to indicate if this is the last event in a batch from the {@link RingBuffer}
     * @throws Exception if the EventHandler would like the exception handled further up the chain.
     */
    @Override
    public void onEvent(DisruptorBindEvent dataEvent, long sequence, boolean endOfBatch) throws Exception {
        DisruptorHandler handler = this.pathMatchingHandlerChainResolver.getExecutionChain(dataEvent);
        handler.doHandler(dataEvent);
    }
}
