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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * @author Zhang Liang
 * @date 2021/1/27
 * @since 1.0
 */

@ConfigurationProperties(prefix = "harbor")
public class HarborProperties {

    /** harbor仓库的ip */
    private String host;

    /** harbor仓库的url */
    private String url;

    /** harbor仓库的登录地址 */
    private String login_address;

    /** harbor仓库登录用户名 */
    private String username;

    /** harbor仓库登录密码 */
    private String password;

    /** 编码格式 */
    private String encoding;

    /** 设置连接超时，单位毫秒 */
    private String connect_timeout;

    /** 设置读取数据超时时间，单位毫秒 */
    private String socket_timeout;

    /** 请求头 用户代理 */
    private String user_agent;


    public String getHost() {
        return host;
    }

    public HarborProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public HarborProperties setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getLogin_address() {
        return login_address;
    }

    public HarborProperties setLogin_address(String login_address) {
        this.login_address = login_address;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public HarborProperties setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public HarborProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public HarborProperties setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getConnect_timeout() {
        return connect_timeout;
    }

    public HarborProperties setConnect_timeout(String connect_timeout) {
        this.connect_timeout = connect_timeout;
        return this;
    }

    public String getSocket_timeout() {
        return socket_timeout;
    }

    public HarborProperties setSocket_timeout(String socket_timeout) {
        this.socket_timeout = socket_timeout;
        return this;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public HarborProperties setUser_agent(String user_agent) {
        this.user_agent = user_agent;
        return this;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("host", host)
                .add("url", url)
                .add("login_address", login_address)
                .add("username", username)
                .add("password", password)
                .add("encoding", encoding)
                .add("connect_timeout", connect_timeout)
                .add("socket_timeout", socket_timeout)
                .add("user_agent", user_agent)
                .toString();
    }
}
