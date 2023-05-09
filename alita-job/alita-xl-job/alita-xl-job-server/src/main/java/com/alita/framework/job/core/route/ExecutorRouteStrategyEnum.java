package com.alita.framework.job.core.route;


import com.alita.framework.job.core.route.strategy.*;
import com.alita.framework.job.utils.I18nUtils;

/**
 * 执行器路由选取策略.
 *
 * <p>
 * 通过枚举类定义路由策略（除了分片广播），并将策略类 router 与枚举类型进行绑定. <br>
 * 通过枚举类型直接能获取到对应路由策略的实现类. <br>
 * 因为分片广播的逻辑代码放在了触发器 {@link com.alita.framework.job.core.scheduler.JobTrigger} 中，没有对应的实现类，所以为null.
 */
public enum ExecutorRouteStrategyEnum {

    FIRST(I18nUtils.getProperty("jobconf_route_first"), new ExecutorRouteFirst()),
    LAST(I18nUtils.getProperty("jobconf_route_last"), new ExecutorRouteLast()),
    ROUND(I18nUtils.getProperty("jobconf_route_round"), new ExecutorRouteRound()),
    RANDOM(I18nUtils.getProperty("jobconf_route_random"), new ExecutorRouteRandom()),
    CONSISTENT_HASH(I18nUtils.getProperty("jobconf_route_consistenthash"), new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED(I18nUtils.getProperty("jobconf_route_lfu"), new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED(I18nUtils.getProperty("jobconf_route_lru"), new ExecutorRouteLRU()),
    FAILOVER(I18nUtils.getProperty("jobconf_route_failover"), new ExecutorRouteFailover()),
    BUSYOVER(I18nUtils.getProperty("jobconf_route_busyover"), new ExecutorRouteBusyover()),
    SHARDING_BROADCAST(I18nUtils.getProperty("jobconf_route_shard"), null);

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    private String title;

    /**
     * 路由策略实现类
     */
    private ExecutorRouter router;

    public String getTitle() {
        return title;
    }
    public ExecutorRouter getRouter() {
        return router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem){
        if (name != null) {
            for (ExecutorRouteStrategyEnum item: ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }


}
