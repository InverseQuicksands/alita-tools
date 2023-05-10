package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.scheduler.JobScheduler;
import com.alita.framework.job.core.biz.model.TriggerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * 路由策略：故障转移.
 *
 * <p>
 * 当调度的机器出现无法调度的情况时，则按照顺序依次进行心跳检测，第一个心跳检测成功的机器选定为目标执行器并发起调度.
 *
 * <p>
 * 实现原理就是通过调用执行器的 beat 接口查看机器的返回状态来判定是否存活，如果不存活则循环下一个继续该步骤，
 * 直到找到可用机器或者无可用机器为止.
 */
public class ExecutorRouteFailover extends ExecutorRouter {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorRouteFailover.class);

    /**
     * 故障转移路由策略.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行机器列表
     * @return address 地址
     */
    @Override
    public String route(TriggerParam triggerParam, List<String> addressList) {
        String responseCode = null;
        for (String address : addressList) {
            try {
                if (address == null) {
                    continue;
                }
                JobHandlerExecutor handlerExecutor = JobScheduler.getJobHandlerExecutor(address);
                // 心跳检测
                responseCode = handlerExecutor.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            if (Objects.equals("00000000", responseCode)) {
                return address;
            }
        }
        // 无可用执行器
        return null;
    }
}
