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

import com.google.common.base.MoreObjects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * <p>
 *
 * @author Zhang Liang
 * @date 2021/1/31
 * @since 1.0
 */

@ConfigurationProperties(prefix = "spring.servlet.multipart")
public class MultipartProperties {

    /** 支持文件写入磁盘 */
    private String fileSizeThreshold = "0";

    /** 单个文件上传最大值(MB必须大写) */
    private String maxFileSize = "100MB";

    /** 单个请求最大限制(MB必须大写) */
    private String maxRequestSize = "100MB";

    /** 上传文件的临时目录 */
    private String location = System.getProperty("user.home") + File.separator + "temp";


    public String getFileSizeThreshold() {
        return fileSizeThreshold;
    }

    public MultipartProperties setFileSizeThreshold(String fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
        return this;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public MultipartProperties setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
        return this;
    }

    public String getMaxRequestSize() {
        return maxRequestSize;
    }

    public MultipartProperties setMaxRequestSize(String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public MultipartProperties setLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fileSizeThreshold", fileSizeThreshold)
                .add("maxFileSize", maxFileSize)
                .add("maxRequestSize", maxRequestSize)
                .add("location", location)
                .toString();
    }
}
