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

package com.alita.framework.platform.autoconfigure.docker;

import com.google.common.base.MoreObjects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * @author Zhang Liang
 * @date 2021/1/30
 * @since 1.0
 */
public class DockerProperties {

    /** docker服务器 */
    @Value("${docker.url}")
    private String url;

    /** docker client CA证书地址，如果使用TLS必填 */
    @Value("${docker.cert.path}")
    private String certPath;

    /** 启用/禁用TLS验证（在http和https协议之间切换） */
    @Value("${docker.tlsVerify}")
    private boolean tlsVerify;

    /** 其他docker配置文件的路径（如.dockercfg） */
    @Value("${docker.config}")
    private String configPath;

    /** API版本 */
    @Value("${docker.api.version}")
    private String apiVersion;

    /** 注册地址 */
    @Value("${docker.registry.url}")
    private String registryUrl;

    /** 注册用户名（用于推送容器） */
    @Value("${docker.registry.username}")
    private String username;

    /** 注册密码 */
    @Value("${docker.registry.password}")
    private String password;

    /** 注册邮箱 */
    @Value("${docker.registry.email}")
    private String email;


    public String getUrl() {
        return url;
    }

    public DockerProperties setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getCertPath() {
        return certPath;
    }

    public DockerProperties setCertPath(String certPath) {
        this.certPath = certPath;
        return this;
    }

    public boolean isTlsVerify() {
        return tlsVerify;
    }

    public DockerProperties setTlsVerify(boolean tlsVerify) {
        this.tlsVerify = tlsVerify;
        return this;
    }

    public String getConfigPath() {
        return configPath;
    }

    public DockerProperties setConfigPath(String configPath) {
        this.configPath = configPath;
        return this;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public DockerProperties setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public DockerProperties setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DockerProperties setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DockerProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public DockerProperties setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", url)
                .add("certPath", certPath)
                .add("tlsVerify", tlsVerify)
                .add("configPath", configPath)
                .add("apiVersion", apiVersion)
                .add("registryUrl", registryUrl)
                .add("username", username)
                .add("password", password)
                .add("email", email)
                .toString();
    }
}
