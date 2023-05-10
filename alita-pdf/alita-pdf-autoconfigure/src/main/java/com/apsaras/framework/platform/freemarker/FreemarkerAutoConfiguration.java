package com.apsaras.framework.platform.freemarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

@AutoConfiguration
@EnableConfigurationProperties({FreeMarkerProperties.class})
public class FreemarkerAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerAutoConfiguration.class);

    public static final String TEMPLATE_LOADER_PATH = "classpath:/static/templates/";

    private final FreeMarkerProperties properties;

    public FreemarkerAutoConfiguration(FreeMarkerProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    public FreeMarkerConfigurationFactoryBean freeMarkerConfig() {
        FreeMarkerConfigurationFactoryBean freeMarkerFactoryBean = new FreeMarkerConfigurationFactoryBean();
        applyProperties(freeMarkerFactoryBean);

        return freeMarkerFactoryBean;
    }

    protected void applyProperties(FreeMarkerConfigurationFactoryBean factoryBean) {
        factoryBean.setTemplateLoaderPaths(TEMPLATE_LOADER_PATH);
        factoryBean.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
        factoryBean.setDefaultEncoding("UTF-8");
        Properties settings = new Properties();
        settings.put("recognize_standard_file_extensions", "true");
        settings.putAll(this.properties.getSettings());
        factoryBean.setFreemarkerSettings(settings);
    }

}
