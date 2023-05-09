package com.alita.framework.event.context.support;

import com.alita.framework.event.annotation.EventRule;
import com.alita.framework.event.autoconfigure.DisruptorProperties;
import com.alita.framework.event.context.EventHandlerDefinition;
import com.alita.framework.event.context.HandlerDefinitionMap;
import com.alita.framework.event.context.event.DisruptorBindEvent;
import com.alita.framework.event.context.handler.DisruptorHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericDisruptorContext extends AbstractDisruptorContext {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDisruptorContext.class);

    private DisruptorProperties disruptorProperties;

    /**
     * DisruptorHandler的实例
     */
    private Map<String, DisruptorHandler<DisruptorBindEvent>> disruptorHandler = new HashMap<>();

    /**
     * 处理器链定义: key-express; value-beanName
     */
    private Map<String, String> handlerDefinition = new HashMap<>();


    public Map<String, DisruptorHandler<DisruptorBindEvent>> getDisruptorHandler() {
        return this.disruptorHandler;
    }

    public Map<String, String> getHandlerDefinition() {
        return this.handlerDefinition;
    }

    public GenericDisruptorContext(DisruptorProperties disruptorProperties) {
        this.disruptorProperties = disruptorProperties;
    }


    /**
     * 获取 application 配置文件的 handler-definitions 属性和应用中声明{@code EventRule}注解类的集合。
     *
     * 配置文件中使用示例：
     * <pre>
     * spring:
     *   disruptor:
     *     enabled: true
     *     ring-buffer: false
     *     ring-buffer-size: 1024
     *     ring-thread-numbers: 4
     *     multi-producer: true
     *     handler-definitions:
     *     - order: 1
     *       definitions: /Event-DC-Output/TagA-Output/** = inDbPreHandler
     *     - order: 2
     *       definitions: /Event-DC-Output/TagB-Output/** = smsPostHandler
     * </pre>
     *
     */
    public void createDisruptorEventHandler() {
        // 获取所有类中声明 @EventRule 注解的 bean.
//        Map<String, Object> beansWithEventRule = super.applicationContext.getBeansWithAnnotation(EventRule.class);
//        LOG.warn("{} not implement DisruptorHandler.class will not register Disruptor Component.", "");

        Map<String, DisruptorHandler> beansOfType = super.applicationContext.getBeansOfType(DisruptorHandler.class);
        if (MapUtils.isNotEmpty(beansOfType)) {
            beansOfType.forEach((beanName, handler) -> {
                EventRule annotationType = super.applicationContext.findAnnotationOnBean(beanName, EventRule.class);
                if (annotationType == null) {
                    LOG.warn("Not found AnnotationType '@EventRule' on {} with Bean name '{}'", handler.getClass(), beanName);
                } else {
                    handlerDefinition.put(annotationType.rule(), beanName);
                }
                disruptorHandler.put(beanName, handler);
            });
        }

        List<EventHandlerDefinition> handlerDefinitionList = disruptorProperties.getHandlerDefinitions();
        if (CollectionUtils.isNotEmpty(handlerDefinitionList)) {
            for (EventHandlerDefinition definition: handlerDefinitionList) {
                Map<String, String> chainDefinitions = this.parseHandlerChainDefinitions(definition.getDefinitions());
                handlerDefinition.putAll(chainDefinitions);
                chainDefinitions.forEach((express, beanName) -> {
                    DisruptorHandler handler = super.applicationContext.getBean(beanName, DisruptorHandler.class);
                    disruptorHandler.put(beanName, handler);
                });
            }
        }
    }


    private Map<String, String> parseHandlerChainDefinitions(String definitions) {
        HandlerDefinitionMap handlerDefinitionMap = new HandlerDefinitionMap();
        handlerDefinitionMap.load(definitions);
        HandlerDefinitionMap.Section section = handlerDefinitionMap.getSection(HandlerDefinitionMap.DEFAULT_SECTION_NAME);
        return section;
    }

}
