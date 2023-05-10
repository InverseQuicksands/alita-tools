package com.alita.framework.id.tinyid.domain;

import java.util.List;

/**
 * <br>
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 13:29
 **/
public class TinyIdClient {

    private String tinyIdToken;
    private String tinyIdServer;
    private List<String> serverList;
    private Integer readTimeout;
    private Integer connectTimeout;

    private static class TinyIdClientSingle {
        private static final TinyIdClient tinyIdClient = new TinyIdClient();
    }

    public static TinyIdClient getInstance() {
        return TinyIdClientSingle.tinyIdClient;
    }

    private TinyIdClient() {

    }

    public String getTinyIdToken() {
        return tinyIdToken;
    }

    public void setTinyIdToken(String tinyIdToken) {
        this.tinyIdToken = tinyIdToken;
    }

    public String getTinyIdServer() {
        return tinyIdServer;
    }

    public void setTinyIdServer(String tinyIdServer) {
        this.tinyIdServer = tinyIdServer;
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public String toString() {
        return "TinyIdClient{" +
                "tinyIdToken='" + tinyIdToken + '\'' +
                ", tinyIdServer='" + tinyIdServer + '\'' +
                ", serverList=" + serverList +
                ", readTimeout=" + readTimeout +
                ", connectTimeout=" + connectTimeout +
                '}';
    }
}
