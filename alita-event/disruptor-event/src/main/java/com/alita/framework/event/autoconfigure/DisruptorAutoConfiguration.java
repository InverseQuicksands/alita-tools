package com.alita.framework.event.autoconfigure;

import com.alita.framework.core.thread.NamedThreadFactory;
import com.alita.framework.event.context.DisruptorContext;
import com.alita.framework.event.context.Lifecycle;
import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.alita.framework.event.context.factory.DisruptorEventFactory;
import com.alita.framework.event.context.handler.AsyncListenerDispatcher;
import com.alita.framework.event.context.handler.PathMatchingHandlerChainResolver;
import com.alita.framework.event.context.support.DefaultLifecycleProcessor;
import com.alita.framework.event.context.support.GenericDisruptorContext;
import com.alita.framework.event.context.translator.DisruptorEventOneArgTranslator;
import com.alita.framework.event.context.translator.DisruptorEventThreeArgTranslator;
import com.alita.framework.event.context.translator.DisruptorEventTwoArgTranslator;
import com.alita.framework.event.context.waitstrategy.WaitStrategys;
import com.alita.framework.event.template.DisruptorTemplate;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;

@Configuration
@ConditionalOnProperty(prefix = "spring.disruptor", value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ DisruptorProperties.class })
public class DisruptorAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public EventFactory<DisruptorBindEvent> eventFactory() {
        return new DisruptorEventFactory();
    }


    @Bean(name = "disruptorLifecycleProcessor")
    @ConditionalOnMissingBean
    public Lifecycle getLifecycle() {
        return new DefaultLifecycleProcessor();
    }


    @Bean
    @ConditionalOnMissingBean
    public DisruptorContext disruptorContext(ConfigurableApplicationContext applicationContext,
                                             DisruptorProperties disruptorProperties) {
        GenericDisruptorContext disruptorContext = new GenericDisruptorContext(disruptorProperties);
        disruptorContext.setApplicationContext(applicationContext);
        disruptorContext.createDisruptorEventHandler();
        return disruptorContext;
    }


    @Bean
    public DisruptorTemplate disruptorTemplate() {
        return new DisruptorTemplate();
    }


    @Bean(name = "disruptorProducer")
    public Disruptor createDisruptor(DisruptorContext disruptorContext,
            DisruptorProperties properties,
            EventFactory<DisruptorBindEvent> eventFactory) {

        ThreadFactory threadFactory = new NamedThreadFactory("Event", false);
        Disruptor<DisruptorBindEvent> disruptor = new Disruptor<DisruptorBindEvent>(eventFactory,
                properties.getRingBufferSize(), threadFactory, ProducerType.SINGLE,
                WaitStrategys.sleepingWaitStrategy);

        GenericDisruptorContext genericDisruptorContext = (GenericDisruptorContext) disruptorContext;
        PathMatchingHandlerChainResolver pathMatchingHandlerChainResolver = new PathMatchingHandlerChainResolver(
                genericDisruptorContext.getDisruptorHandler(), genericDisruptorContext.getHandlerDefinition());

        final AsyncListenerDispatcher listenerDispatcher = new AsyncListenerDispatcher(pathMatchingHandlerChainResolver);
        disruptor.handleEventsWith(listenerDispatcher);
        disruptor.start();

        return disruptor;
    }


    @Bean
    @ConditionalOnMissingBean
    public EventTranslatorOneArg<DisruptorBindEvent, DisruptorBindEvent> oneArgEventTranslator() {
        return new DisruptorEventOneArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventTranslatorTwoArg<DisruptorBindEvent, String, String> twoArgEventTranslator() {
        return new DisruptorEventTwoArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventTranslatorThreeArg<DisruptorBindEvent, String, String, String> threeArgEventTranslator() {
        return new DisruptorEventThreeArgTranslator();
    }


}
