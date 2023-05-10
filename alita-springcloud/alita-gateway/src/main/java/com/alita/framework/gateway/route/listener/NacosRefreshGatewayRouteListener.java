package com.alita.framework.gateway.route.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alita.framework.gateway.route.repository.DynamicRouteDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 监听 Nacos 动态路由配置
 */
@Component
@EnableConfigurationProperties(NacosGatewayRoutesProperties.class)
public class NacosRefreshGatewayRouteListener implements InitializingBean, ApplicationListener<ContextRefreshedEvent>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(NacosRefreshGatewayRouteListener.class);

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private DynamicRouteDefinitionRepository routeDefinitionRepository;

    private final NacosGatewayRoutesProperties nacosGatewayRoutesProperties;

    public NacosRefreshGatewayRouteListener(NacosGatewayRoutesProperties nacosGatewayRoutesProperties) {
        this.nacosGatewayRoutesProperties = nacosGatewayRoutesProperties;
    }

    /**
     * 监听 Nacos 路由配置，如果修改发布后将刷新 Gateway 内存路由信息.
     *
     * @throws NacosException
     */
    public void refreshGatewayRouteListener() throws NacosException {
        ConfigService configService = nacosConfigManager.getConfigService();
        configService.addListener(nacosGatewayRoutesProperties.getDataId(), nacosGatewayRoutesProperties.getGroup(), new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String routeInfo) {
                logger.debug("【update gateway route info】: {}", routeInfo);
                List<RouteDefinition> routeDefinitions = JSON.parseArray(routeInfo, RouteDefinition.class);
                routeDefinitionRepository.updateAll(routeDefinitions);
            }
        });
    }


    /**
     * ioc 初始化时启动 Nacos 监听.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        refreshGatewayRouteListener();
    }

    /**
     * 因应用启动时 Gateway 的路由信息在 Nacos 其他 dataId 内，
     * 故需要在 ioc 初始化完成后将 Nacos 的路由信息全量刷新到 Gateway 的内存中.
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ConfigService configService = nacosConfigManager.getConfigService();
        String routeInfo = null;
        try {
            routeInfo = configService.getConfig(nacosGatewayRoutesProperties.getDataId(), nacosGatewayRoutesProperties.getGroup(), 5000);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        logger.debug("【init gateway route info】: {}", routeInfo);
        List<RouteDefinition> routeDefinitions = JSON.parseArray(routeInfo, RouteDefinition.class);
        routeDefinitionRepository.addAll(routeDefinitions);
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
        return Ordered.HIGHEST_PRECEDENCE + 1000;
    }
}
