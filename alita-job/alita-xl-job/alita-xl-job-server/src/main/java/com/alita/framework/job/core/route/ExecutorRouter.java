package com.alita.framework.job.core.route;

import com.alita.framework.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * 执行器路由策略
 *
 * <p>
 * 路由策略在创建任务时选择，默认值为【第一个】.<br>
 * 当任务的执行器路由地址只有一个时，不需要进行路由判断，建议选取策略【第一个】.<br>
 * 当执行器地址有多个时，会根据选择的路由策略，进行执行路由选择，选取一个地址进行调度.<br>
 * 其中【分片广播】比较特殊，会对执行器下所有路由进行调用执行.
 */
public abstract class ExecutorRouter {

    /**
     * 抽象路由方法.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行机器列表
     * @return address 地址
     */
    public abstract String route(TriggerParam triggerParam, List<String> addressList);
}
