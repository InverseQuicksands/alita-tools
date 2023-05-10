package com.alita.framework.platform.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;


public class CustomizeInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeInstantiationAwareBeanPostProcessor.class);

    /**
     * Apply this BeanPostProcessor <i>before the target bean gets instantiated</i>.
     * The returned bean object may be a proxy to use instead of the target bean,
     * effectively suppressing default instantiation of the target bean.
     * <p>If a non-null object is returned by this method, the bean creation process
     * will be short-circuited. The only further processing applied is the
     * {@link #postProcessAfterInitialization} callback from the configured
     * {@link BeanPostProcessor BeanPostProcessors}.
     * <p>This callback will be applied to bean definitions with their bean class,
     * as well as to factory-method definitions in which case the returned bean type
     * will be passed in here.
     * <p>Post-processors may implement the extended
     * {@link SmartInstantiationAwareBeanPostProcessor} interface in order
     * to predict the type of the bean object that they are going to return here.
     * <p>The default implementation returns {@code null}.
     *
     * @param beanClass the class of the bean to be instantiated
     * @param beanName  the name of the bean
     * @return the bean object to expose instead of a default instance of the target bean,
     * or {@code null} to proceed with default instantiation
     * @throws BeansException in case of errors
     * @see #postProcessAfterInstantiation
     * @see AbstractBeanDefinition#getBeanClass()
     * @see AbstractBeanDefinition#getFactoryMethodName()
     *
     * postProcessBeforeInstantiation在doCreateBean之前调用，也就是在bean实例化之前调用的，
     * 英文源码注释解释道该方法的返回值会替换原本的Bean作为代理
     *
     * 自身方法，是最先执行的方法，它在目标对象实例化之前调用，该方法的返回值类型是Object，
     * 我们可以返回任何类型的值。由于这个时候目标对象还未实例化，所以这个返回值可以用来代替原本该生成的目标对象的实例(比如代理对象)。
     * 如果该方法的返回值代替原本该生成的目标对象，后续只有postProcessAfterInitialization方法会调用，其它方法不再调用；
     * 否则按照正常的流程走
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        logger.debug("优先级：4，在目标对象实例化之前执行,Bean对象还未生成: Class ---> [{}], beanName ---> [{}] ", beanClass, beanName);
        return null;
    }

    /**
     * Perform operations after the bean has been instantiated, via a constructor or factory method,
     * but before Spring property population (from explicit properties or autowiring) occurs.
     * <p>This is the ideal callback for performing custom field injection on the given bean
     * instance, right before Spring's autowiring kicks in.
     * <p>The default implementation returns {@code true}.
     *
     * @param bean     the bean instance created, with properties not having been set yet
     * @param beanName the name of the bean
     * @return {@code true} if properties should be set on the bean; {@code false}
     * if property population should be skipped. Normal implementations should return {@code true}.
     * Returning {@code false} will also prevent any subsequent InstantiationAwareBeanPostProcessor
     * instances being invoked on this bean instance.
     * @throws BeansException in case of errors
     * @see #postProcessBeforeInstantiation
     *
     * 在目标对象实例化之后调用，这个时候对象已经被实例化，但是该实例的属性还未被设置，都是null。
     * 因为它的返回值是决定要不要调用postProcessPropertyValues方法的其中一个因素（因为还有一个因素是mbd.getDependencyCheck()）；
     * 如果该方法返回false,并且不需要check，那么postProcessPropertyValues就会被忽略不执行；
     * 如果返回true，postProcessPropertyValues就会被执行
     * 换句话说：不会进行"方法注入"
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        logger.debug("优先级：5，在目标对象实例化之后执行,Bean对象已经创建: bean ---> [{}], beanName ---> [{}] ", bean, beanName);
        return false;
    }

    /**
     * Post-process the given property values before the factory applies them
     * to the given bean, without any need for property descriptors.
     * <p>Implementations should return {@code null} (the default) if they provide a custom
     * {@link #postProcessPropertyValues} implementation, and {@code pvs} otherwise.
     * In a future version of this interface (with {@link #postProcessPropertyValues} removed),
     * the default implementation will return the given {@code pvs} as-is directly.
     *
     * @param pvs      the property values that the factory is about to apply (never {@code null})
     * @param bean     the bean instance created, but whose properties have not yet been set
     * @param beanName the name of the bean
     * @return the actual property values to apply to the given bean (can be the passed-in
     * PropertyValues instance), or {@code null} which proceeds with the existing properties
     * but specifically continues with a call to {@link #postProcessPropertyValues}
     * (requiring initialized {@code PropertyDescriptor}s for the current bean class)
     * @throws BeansException in case of errors
     * @see #postProcessPropertyValues
     * @since 5.1
     *
     * 对属性值进行修改，如果postProcessAfterInstantiation方法返回false，该方法可能不会被调用。可以在该方法内对属性值进行修改
     */
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return pvs;
    }


    /**
     * Apply this {@code BeanPostProcessor} to the given new bean instance <i>before</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * <p>The default implementation returns the given {@code bean} as-is.
     *
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one;
     * if {@code null}, no subsequent BeanPostProcessors will be invoked
     * @throws BeansException in case of errors
     * @see InitializingBean#afterPropertiesSet
     *
     * BeanPostProcessor接口中的方法,在Bean的自定义初始化方法之前执行
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        logger.debug("优先级：6，在目标对象初始化方法之前执行, Bean对象已经存在: bean ---> [{}], beanName ---> [{}] ", bean, beanName);
        return bean;
    }

    /**
     * Apply this {@code BeanPostProcessor} to the given new bean instance <i>after</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
     * instance and the objects created by the FactoryBean (as of Spring 2.0). The
     * post-processor can decide whether to apply to either the FactoryBean or created
     * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
     * <p>This callback will also be invoked after a short-circuiting triggered by a
     * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
     * in contrast to all other {@code BeanPostProcessor} callbacks.
     * <p>The default implementation returns the given {@code bean} as-is.
     *
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one;
     * if {@code null}, no subsequent BeanPostProcessors will be invoked
     * @throws BeansException in case of errors
     * @see InitializingBean#afterPropertiesSet
     * @see FactoryBean
     *
     * BeanPostProcessor接口中的方法,在Bean的自定义初始化方法执行完成之后执行
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.debug("优先级：7，在目标对象初始化方法之后执行, Bean对象已经存在: bean ---> [{}], beanName ---> [{}] ", bean, beanName);
        return bean;
    }
}
