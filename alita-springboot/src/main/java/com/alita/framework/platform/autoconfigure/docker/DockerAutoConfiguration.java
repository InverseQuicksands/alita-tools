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

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.LocalDirectorySSLConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.transport.SSLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Indexed;


/**
 * Docker 配置
 * <p>通过{@link DockerClient}连接Docker服务器，并配置{@code Harbor}连接
 *
 * @author Zhang Liang
 * @date 2021/1/27
 * @since 1.0
 *
 * @see DockerClient
 * @see ApacheDockerHttpClient
 */

@Configuration
@Import(value = {DockerProperties.class})
@EnableConfigurationProperties(value = {HarborProperties.class})
@ConditionalOnClass(value = {DockerClient.class, AuthConfig.class})
@ConditionalOnProperty(prefix = "docker", name = "url")
@Indexed
public class DockerAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DockerAutoConfiguration.class);


    @Autowired
    private DockerProperties dockerProperties;

    @Autowired
    private HarborProperties harborProperties;



    /**
     * 获取docker链接
     *
     * @return {@code DockerClient}
     */
    @Bean
    @ConditionalOnProperty(prefix = "harbor", name = "login_address")
    public DockerClient getDockerClient() {
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerProperties.getUrl())
                .withDockerTlsVerify(dockerProperties.isTlsVerify())
                .withDockerCertPath(dockerProperties.getCertPath())
                .withDockerConfig(dockerProperties.getConfigPath())
                .withApiVersion(dockerProperties.getApiVersion())
                .withRegistryUrl(harborProperties.getLogin_address())
                .withRegistryUsername(harborProperties.getUsername())
                .withRegistryPassword(harborProperties.getPassword())
                .build();

        // 配置docker客户端连接及证书信息
        SSLConfig sslConfig = new LocalDirectorySSLConfig(dockerProperties.getCertPath());

        // Docker Http Client
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(sslConfig)
                .build();

        // 目前getInstance方法中，只有此方法被推荐
        return DockerClientImpl.getInstance(dockerClientConfig, httpClient);
    }


    @Bean
    @ConditionalOnProperty(prefix = "harbor", name = "login_address")
    public AuthConfig autoConfig() {
        //Harbor登录信息
        return new AuthConfig()
                .withRegistryAddress(harborProperties.getLogin_address())
                .withUsername(harborProperties.getUsername())
                .withPassword(harborProperties.getPassword());

    }

}
