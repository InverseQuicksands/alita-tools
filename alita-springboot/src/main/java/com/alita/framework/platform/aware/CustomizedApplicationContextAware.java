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

package com.alita.framework.platform.aware;

import com.alita.framework.platform.annotation.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Map;

/**
 * <p>
 *
 * @author Zhang Liang
 * @date 2021/1/30
 * @since 1.0
 */
@Configuration
public class CustomizedApplicationContextAware implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CustomizedApplicationContextAware.class);

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        scanDSLAnnotation();
    }

    private void scanDSLAnnotation() {
        Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(DSL.class);
        beansWithAnnotation.forEach((key, value) -> {
            Class<?> aClass = value.getClass();
            logger.debug("load current IOC container {}", aClass);
            ReflectionUtils.doWithMethods(aClass, method -> {

            });
        });
    }

}
