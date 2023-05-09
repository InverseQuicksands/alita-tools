package com.alita.framework.platform.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class CustomizeBeanImport implements ImportBeanDefinitionRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeBeanImport.class);

    /**
     * Register bean definitions as necessary based on the given annotation metadata of
     * the importing {@code @Configuration} class.
     * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
     * registered here, due to lifecycle constraints related to {@code @Configuration}
     * class processing.
     * <p>The default implementation is empty.
     *
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry               current bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        logger.debug("registry other bean");
//        AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(RegistryConfigBean.class);
//        registry.registerBeanDefinition("registryConfigBean", beanDefinition);
    }
}
