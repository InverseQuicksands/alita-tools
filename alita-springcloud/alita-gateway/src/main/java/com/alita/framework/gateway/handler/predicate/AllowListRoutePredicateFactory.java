package com.alita.framework.gateway.handler.predicate;

import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 白名单断言工厂.
 *
 * <pre>
 *   predicates:
 *     - name: AllowList
 *       args:
 *         arg1: /demo/list
 * </pre>
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-18 19:57
 */
public class AllowListRoutePredicateFactory extends AbstractRoutePredicateFactory<AllowListRoutePredicateFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AllowListRoutePredicateFactory.class);

    public AllowListRoutePredicateFactory() {
        super(Config.class);
    }

    /**
     * 使用yaml配置文件时的数据映射方式.
     *
     * <p>返回值类型ShortcutType共有三个属性，各个属性决定了框架自动生成config对象时的规则。
     * 当ShortcutType.DEFAULT类型时，框架将yaml中的值，映射到config中名称相同的变量中；<br>
     * 当ShortcutType.GATHER_LIST类型时，框架将yaml中的值聚合为List，
     * 映射到shortcutFieldOrder()的返回值对应config的变量中，
     * 此时shortcutFieldOrder()返回的List的长度必须为1.
     *
     * @return
     */
    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    /**
     * Returns hints about the number of args and the order for shortcut parsing.
     * 当shortcutType()返回值为ShortcutType.GATHER_LIST时被使用，用于指定Config中List变量的名称.
     *
     * @return the list of hints
     */
    @Override
    public List<String> shortcutFieldOrder() {
        // 使用GATHER_LIST映射方式时，指定对应List的变量名（即Config对象中的List变量）
        return Collections.singletonList("allowListRoutes");
    }


    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        // config的变量值，框架会根据yaml配置文件进行设置
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                // 自定义的断言规则，返回false则表示断言匹配成功
                String requestPath = exchange.getRequest().getPath().value();
                logger.debug("request path: {}", requestPath);
                boolean match = config.allowListRoutes.stream().anyMatch(allowRoute ->
                        requestPath.contains(allowRoute) || requestPath.matches(allowRoute));

                return !match;
            }
        };
    }

    public static class Config{

        @NotEmpty
        private List<String> allowListRoutes = new ArrayList<>();

        public List<String> getAllowListRoutes() {
            return allowListRoutes;
        }

        public void setAllowListRoutes(List<String> allowListRoutes) {
            this.allowListRoutes = allowListRoutes;
        }
    }
}
