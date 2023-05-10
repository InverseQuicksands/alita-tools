package com.alita.framework.gateway.route.repository;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway 动态路由信息维护.
 *
 * <p>Spring Cloud Gateway 默认将路由信息保存在内存中，详见：{@link GatewayAutoConfiguration#inMemoryRouteDefinitionRepository()}.
 * 每次更新路由信息需要重新启动应用才能刷新路由。结合 Nacos 时虽然重新发布会更新应用配置，但并不会更新 Spring Cloud Gateway 中的路由信息，需要手动更新。
 *
 * @see RouteDefinitionRepository
 * @see InMemoryRouteDefinitionRepository
 */
@Component
public class DynamicRouteDefinitionRepository implements ApplicationEventPublisherAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRouteDefinitionRepository.class);

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
    @Autowired
    private RouteDefinitionRepository routeDefinitionRepository;

    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    /**
     * 新增路由信息.
     *
     * @param routeDefinition
     */
    public void add(@NotNull RouteDefinition routeDefinition) {
        this.routeDefinitionRepository.save(Mono.just(routeDefinition)).subscribe();
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 批量新增路由信息.
     *
     * @param routeDefinitions
     */
    public void addAll(List<RouteDefinition> routeDefinitions) {
        for (RouteDefinition routeDefinition: routeDefinitions) {
            this.routeDefinitionRepository.save(Mono.just(routeDefinition)).subscribe();
        }
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 修改路由信息.
     *
     * @param routeDefinition
     */
    public void update(@NotNull RouteDefinition routeDefinition) {
        this.routeDefinitionRepository.delete(Mono.just(routeDefinition.getId())).subscribe();
        this.routeDefinitionRepository.save(Mono.just(routeDefinition)).subscribe();
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 批量修改路由信息.
     *
     * @param routeDefinitionList
     */
    public void updateAll(List<RouteDefinition> routeDefinitionList) {
        List<RouteDefinition> routeDefinitions = this.routeDefinitionRepository.getRouteDefinitions().buffer().blockFirst();
        // 先删除旧的路由信息
        if (!CollectionUtils.isEmpty(routeDefinitions)) {
            routeDefinitions.stream().forEach(routeDefinition -> {
                delete(routeDefinition);
            });
        }
        routeDefinitionList.stream().forEach(routeDefinition -> {
            add(routeDefinition);
        });
    }

    /**
     * 删除路由信息.
     *
     * @param routeDefinition
     */
    public void delete(@NotNull RouteDefinition routeDefinition) {
        this.routeDefinitionRepository.delete(Mono.just(routeDefinition.getId())).subscribe();
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 清空 Gateway 内存中的路由信息.
     */
    public void clear() {
        List<RouteDefinition> routeDefinitions = this.routeDefinitionRepository.getRouteDefinitions().buffer().blockFirst();
        if (!CollectionUtils.isEmpty(routeDefinitions)) {
            for (RouteDefinition routeDefinition: routeDefinitions) {
                delete(routeDefinition);
            }
        }
    }

}
