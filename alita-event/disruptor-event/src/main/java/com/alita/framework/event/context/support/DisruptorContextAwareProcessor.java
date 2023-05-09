package com.alita.framework.event.context.support;

import com.alita.framework.event.context.DisruptorContext;
import com.alita.framework.event.context.DisruptorContextAware;
import com.alita.framework.event.context.DisruptorEventPublisherAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;


/**
 * DisruptorEvent扩展接口.
 * <p>用于扩展{@link DisruptorEventPublisherAware}、{@link DisruptorContextAware} 自定义实现.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 * @see DisruptorEventPublisherAware
 * @see DisruptorContextAware
 */
@Component
public class DisruptorContextAwareProcessor implements BeanPostProcessor {

    private DisruptorContext disruptorContext;

    private ConfigurableApplicationContext applicationContext;

    public DisruptorContextAwareProcessor(DisruptorContext disruptorContext, ConfigurableApplicationContext applicationContext) {
        this.disruptorContext = disruptorContext;
        this.applicationContext = applicationContext;
    }


    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        if (!(bean instanceof DisruptorEventPublisherAware || bean instanceof DisruptorContextAware)){
            return bean;
        }

        invokeAwareInterfaces(bean);
        return bean;
    }


    /**
     * 扩展 DisruptorEventPublisherAware 接口.
     *
     * @param bean
     */
    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof DisruptorEventPublisherAware) {
                ((DisruptorEventPublisherAware) bean).setDisruptorEventPublisher(this.disruptorContext);
            }
            if (bean instanceof DisruptorContextAware) {
                ((DisruptorContextAware) bean).setDisruptorContext(this.disruptorContext);
            }
        }
    }

}
