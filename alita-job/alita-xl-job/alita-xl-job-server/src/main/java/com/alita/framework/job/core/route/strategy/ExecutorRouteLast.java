package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * 路由策略：最后一台.
 *
 * <p>
 * 固定选择最后一个机器.
 */
public class ExecutorRouteLast extends ExecutorRouter {

    /**
     * 路由策略：最后一台.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行机器列表
     * @return address 地址
     */
    @Override
    public String route(TriggerParam triggerParam, List<String> addressList) {
        return addressList.get(addressList.size()-1);
    }

}
