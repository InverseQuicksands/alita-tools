package com.alita.framework.platform.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class CustomizeSmartInitializingSingleton implements SmartInitializingSingleton {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeSmartInitializingSingleton.class);

    /**
     * Invoked right at the end of the singleton pre-instantiation phase,
     * with a guarantee that all regular singleton beans have been created
     * already. {@link ListableBeanFactory#getBeansOfType} calls within
     * this method won't trigger accidental side effects during bootstrap.
     * <p><b>NOTE:</b> This callback won't be triggered for singleton beans
     * lazily initialized on demand after {@link BeanFactory} bootstrap,
     * and not for any other bean scope either. Carefully use it for beans
     * with the intended bootstrap semantics only.
     */
    @Override
    public void afterSingletonsInstantiated() {
        logger.debug("在所有单例Bean加载完后调用, 因此你可以通过ApplicationContext 来拿到所有加载好的Bean，并对他们进行一些处理");
    }
}
