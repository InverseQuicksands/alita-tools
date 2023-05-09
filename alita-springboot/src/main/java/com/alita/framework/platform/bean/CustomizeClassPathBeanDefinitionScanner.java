package com.alita.framework.platform.bean;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Set;

public class CustomizeClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {


    /**
     * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
     *
     * @param registry the {@code BeanFactory} to load bean definitions into, in the form
     *                 of a {@code BeanDefinitionRegistry}
     */
    public CustomizeClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }


    /**
     * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
     * <p>If the passed-in bean factory does not only implement the
     * {@code BeanDefinitionRegistry} interface but also the {@code ResourceLoader}
     * interface, it will be used as default {@code ResourceLoader} as well. This will
     * usually be the case for {@link ApplicationContext}
     * implementations.
     * <p>If given a plain {@code BeanDefinitionRegistry}, the default {@code ResourceLoader}
     * will be a {@link PathMatchingResourcePatternResolver}.
     * <p>If the passed-in bean factory also implements {@link EnvironmentCapable} its
     * environment will be used by this reader.  Otherwise, the reader will initialize and
     * use a {@link StandardEnvironment}. All
     * {@code ApplicationContext} implementations are {@code EnvironmentCapable}, while
     * normal {@code BeanFactory} implementations are not.
     *
     * @param registry          the {@code BeanFactory} to load bean definitions into, in the form
     *                          of a {@code BeanDefinitionRegistry}
     * @param useDefaultFilters whether to include the default filters for the
     *                          {@link Component @Component},
     *                          {@link Repository @Repository},
     *                          {@link Service @Service}, and
     *                          {@link Controller @Controller} stereotype annotations
     * @see #setResourceLoader
     * @see #setEnvironment
     */
    public CustomizeClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }


    public void addIncludeFilter(TypeFilter includeFilter) {
        super.addIncludeFilter(includeFilter);
    }

    /**
     * Perform a scan within the specified base packages,
     * returning the registered bean definitions.
     * <p>This method does <i>not</i> register an annotation config processor
     * but rather leaves this up to the caller.
     *
     * @param basePackages the packages to check for annotated classes
     * @return set of beans registered if any for tooling registration purposes (never {@code null})
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }
}
