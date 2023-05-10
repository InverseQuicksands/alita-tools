/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.alita.framework.platform.processor;

import com.alita.framework.platform.exception.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * 初始化环境配置
 *
 * @author Zhang Liang
 * @date 2021/2/26
 * @since 1.0
 */

public class AeolusPlatformEnvironmentProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * 注册中心环境变量名称.
     */
    public static final String REGISTRY_SERVER_URL = "REGISTRY_SERVER_URL";


    /**
     * Post-process the given {@code environment}.
     *
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (System.getProperty(REGISTRY_SERVER_URL) != null) {
            return;
        }
        initRegistryGlobalToEnv(environment);
        initLogConfigurationToEnv(environment);
    }


    /**
     * 注册全局环境变量.
     *
     * @param environment configurableEnvironment.
     */
    private void initRegistryGlobalToEnv(ConfigurableEnvironment environment) {
        String configFile = "";
        try {
            if (isInstallRuntimeEnv()) {
                configFile = System.getenv("CRP_HOME") + File.separator + "config" +
                        File.separator + "dev.properties";
            }

            Properties props = new Properties();
            props.load(new FileInputStream(configFile));
            props.setProperty(REGISTRY_SERVER_URL,
                    props.getProperty("registry.url", "127.0.0.1:8848"));

            PropertiesPropertySource propertySource = new PropertiesPropertySource("aeolus", props);
            environment.getPropertySources().addLast(propertySource);

            System.setProperty(REGISTRY_SERVER_URL,
                    props.getProperty("registry.url", "127.0.0.1:8848"));
        } catch (IOException | SystemException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 是否安装运行时环境
     *
     * @return
     */
    private boolean isInstallRuntimeEnv() throws SystemException {
        if (Objects.isNull(System.getenv("CRP_HOME"))) {
            throw new SystemException("RuntimeEnv must not be null!");
        }

        return true;
    }


    /**
     * 初始化日志目录.
     *
     * @param environment configurableEnvironment.
     */
    private void initLogConfigurationToEnv(ConfigurableEnvironment environment) {
        System.setProperty("dubbo.application.logger", "log4j2");

        String logPath = "";
        try {
            if (isInstallRuntimeEnv()) {
                logPath = System.getenv("CRP_HOME");
            }
        } catch (SystemException ex) {
            ex.printStackTrace();
        }

        if (StringUtils.isNotEmpty(logPath)) {
            System.setProperty("LOG_FILE_PATH", logPath + File.separator + "logs" + File.separator + "provider");
        } else {
            System.setProperty("LOG_FILE_PATH", System.getProperty("user.home") + File.separator +
                    "logs" + File.separator + "provider");
        }

        // nacos 日志目录
        String nacosLogPath = System.getProperty("nacos.logging.path",
                System.getProperty("LOG_FILE_PATH") + File.separator + "nacos" + File.separator + "logs");
        System.setProperty("nacos.logging.path", nacosLogPath);

        // nacos 本地缓存配置目录(config/naming)
        System.setProperty("JM.SNAPSHOT.PATH",
                System.getProperty("LOG_FILE_PATH") + File.separator + "nacos" + File.separator + "config");
        System.setProperty("com.alibaba.nacos.naming.cache.dir",
                System.getProperty("LOG_FILE_PATH") + File.separator + "nacos" + File.separator + "naming");

        // RocketMQ-Client 4.2.0 Log4j2 配置文件冲突问题解决：https://www.jianshu.com/p/b30ae6dd3811
        System.setProperty("rocketmq.client.log.loadconfig", "false");
        //  RocketMQ-Client 4.3 设置默认为 slf4j
        System.setProperty("rocketmq.client.logUseSlf4j", "true");

        // 关闭 nacos 默认的 log 配置
//        System.setProperty("nacos.logging.default.config.enabled", "false");
    }


    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+1;
    }
}
