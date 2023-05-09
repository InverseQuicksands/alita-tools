package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.biz.model.TriggerParam;

import java.util.List;
import java.util.Random;

/**
 * 路由策略：随机.
 *
 * <p>
 * 随机选择在线的机器.<br>
 * 通过 Random 进行随机数获取，决定路由下标返回地址.
 */
public class ExecutorRouteRandom extends ExecutorRouter {

    private static Random localRandom = new Random();

    /**
     * 随机路由策略.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行机器列表
     * @return address 地址
     */
    @Override
    public String route(TriggerParam triggerParam, List<String> addressList) {
        String address = addressList.get(localRandom.nextInt(addressList.size()));
        return address;
    }

}
