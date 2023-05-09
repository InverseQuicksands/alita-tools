package com.alita.framework.gateway.route.listener;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = NacosGatewayRoutesProperties.NACOSGATEWAYPREFIX)
public class NacosGatewayRoutesProperties {

    public static final String NACOSGATEWAYPREFIX = "gateway.routes.config";

    private String dataId;

    private String group;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "NacosGatewayProperties{" +
                "dataId='" + dataId + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
