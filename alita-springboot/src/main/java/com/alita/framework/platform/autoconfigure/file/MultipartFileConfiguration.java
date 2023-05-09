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

package com.alita.framework.platform.autoconfigure.file;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Indexed;
import org.springframework.util.unit.DataSize;

/**
 * <p>
 *
 * @author Zhang Liang
 * @date 2021/1/31
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(value = {MultipartProperties.class})
@Indexed
public class MultipartFileConfiguration {

    @Autowired
    private MultipartProperties multipartProperties;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize(DataSize.parse(multipartProperties.getMaxFileSize()));
        // 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.parse(multipartProperties.getMaxRequestSize()));
        // 规避找不到临时目录问题
        factory.setLocation(multipartProperties.getLocation());
        // 支持文件写入磁盘
        factory.setFileSizeThreshold(DataSize.parse(multipartProperties.getFileSizeThreshold()));

        return factory.createMultipartConfig();
    }

}
