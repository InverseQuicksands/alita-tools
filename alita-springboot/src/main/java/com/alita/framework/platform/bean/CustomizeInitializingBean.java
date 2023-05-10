package com.alita.framework.platform.bean;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CustomizeInitializingBean implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeInitializingBean.class);

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     * essential property) or if initialization fails for any other reason
     *
     * 执行顺序是
     * postProcessBeforeInitialization
     * @PostConstruct > InitializingBean > init-method
     * postProcessAfterInitialization
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("这个触发点是在postProcessAfterInitialization之前");
    }

    @PostConstruct
    public void prinfPostConstruct() {
        logger.debug("这个触发点是在postProcessBeforeInitialization之后，InitializingBean.afterPropertiesSet之前");
    }
}
