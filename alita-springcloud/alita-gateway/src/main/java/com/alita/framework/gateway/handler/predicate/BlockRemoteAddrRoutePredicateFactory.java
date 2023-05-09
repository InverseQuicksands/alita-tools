package com.alita.framework.gateway.handler.predicate;

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;


/**
 * 黑名单断言工.
 *
 * <p>断言工厂命名规则：必须以 {@code RoutePredicateFactory} 结尾.
 * {@code RouteDefinitionRouteLocator#initFactories}
 * 初始化时截取 {@code RoutePredicateFactory} 结尾的断言工厂注入到容器中。
 *
 * <pre>
 *   predicates:
 *     - name: BlockRemoteAddr
 *       args:
 *         _genkey_0: 169.254.183.1/18
 *         _genkey_1: 172.17.31.1/18
 * </pre>
 *
 * @see org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator
 * @see org.springframework.cloud.gateway.support.NameUtils
 * @date 2023-03-17 10:11
 */
public class BlockRemoteAddrRoutePredicateFactory extends AbstractRoutePredicateFactory<BlockRemoteAddrRoutePredicateFactory.Config> {

    public BlockRemoteAddrRoutePredicateFactory() {
        super(Config.class);
    }

    @NotNull
    private List<IpSubnetFilterRule> convert(List<String> ipList) {
        List<IpSubnetFilterRule> ipSubnetFilterRuleList = new ArrayList<>();
        for (String ip : ipList) {
            addIpSubnetFilterRuleList(ipSubnetFilterRuleList, ip);
        }
        return ipSubnetFilterRuleList;
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
        return Collections.singletonList("ipList");
    }


    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        // IpSubnetFilterRule是Netty中定义的IP过滤规则
        List<IpSubnetFilterRule> ipSubnetFilterRuleList = convert(config.ipList);
        // 自定义的断言规则，返回false则表示断言匹配成功
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                InetSocketAddress remoteAddress = config.remoteAddressResolver.resolve(exchange);
                if (remoteAddress != null && remoteAddress.getAddress() != null) {
                    //只要符合任意一个规则就返回false，与RemoteAddrRoutePredicateFactory相反
                    boolean result = ipSubnetFilterRuleList.stream()
                            .anyMatch(filterRule -> filterRule.matches(remoteAddress));
                    return !result;
                }
                //如果没有匹配所有规则，则通过
                return true;
            }
        };
    }

    private void addIpSubnetFilterRuleList(List<IpSubnetFilterRule> ipSubnetFilterRules, String ip) {
        //判断是否配置了IP段，如果没有则默认为最大为32，如配置172.15.32.15，则被修改为172.15.32.15/32
        if (!ip.contains("/")) { // no netmask, add default
            ip = ip + "/32";
        }
        // 假设配置的为 172.15.32.15/18
        // 根据'/'分割  [0]:172.15.32.15  [1]:18
        String[] ipAddressCidrPrefix = ip.split("/", 2);
        String ipAddress = ipAddressCidrPrefix[0];
        int cidrPrefix = Integer.parseInt(ipAddressCidrPrefix[1]);

        //设置拒绝规则
        ipSubnetFilterRules.add(new IpSubnetFilterRule(ipAddress, cidrPrefix, IpFilterRuleType.REJECT));
    }

    public static class Config{
        /**
         * 可配置多个IP/IP段
         */
        @NotEmpty
        private List<String> ipList = new ArrayList<>();
        /**
         * 用来解析客户端IP
         */
        @NotNull
        private RemoteAddressResolver remoteAddressResolver = new RemoteAddressResolver() {};

        public List<String> getIpList() {
            return ipList;
        }

        public Config setIpList(List<String> ipList) {
            this.ipList = ipList;
            return this;
        }

        public Config setSources(String... sources) {
            this.ipList = Arrays.asList(sources);
            return this;
        }

        public Config setRemoteAddressResolver(RemoteAddressResolver remoteAddressResolver) {
            this.remoteAddressResolver = remoteAddressResolver;
            return this;
        }
    }
}