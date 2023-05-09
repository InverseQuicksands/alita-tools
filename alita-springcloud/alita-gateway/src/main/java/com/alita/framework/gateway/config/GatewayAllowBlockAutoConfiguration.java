package com.alita.framework.gateway.config;

import com.alita.framework.gateway.handler.predicate.AllowListRoutePredicateFactory;
import com.alita.framework.gateway.handler.predicate.BlockRemoteAddrRoutePredicateFactory;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 黑/白名单配置类.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-16 13:39
 */
@Configuration(proxyBeanMethods = false)
public class GatewayAllowBlockAutoConfiguration {

    @Bean
    @ConditionalOnEnabledPredicate
    public BlockRemoteAddrRoutePredicateFactory blockRemoteAddrRoutePredicateFactory() {
        return new BlockRemoteAddrRoutePredicateFactory();
    }

    @Bean
    @ConditionalOnEnabledPredicate
    public AllowListRoutePredicateFactory allowListRoutePredicateFactory() {
        return new AllowListRoutePredicateFactory();
    }

}
