package com.alita.framework.event.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.stereotype.Component;

@Component
public class BeanLocator implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 返回ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 返回bean
     *
     * @param beanName beanName
     * @return bean
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 获取bean
     *
     * @param tClass
     * @param <T> 泛型
     * @return bean
     */
    public static <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }

    /**
     * 获取bean
     * @param tClass
     * @param  name 名称
     * @param <T> 泛型
     * @return T 泛型
     */
    public static <T> T getBean(String name, Class<T> tClass) {
        return getApplicationContext().getBean(name, tClass);
    }

}
