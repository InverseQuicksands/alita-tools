package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.scheduler.JobScheduler;
import com.alita.framework.job.core.biz.model.TriggerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * 路由策略：忙碌转移.
 *
 * <p>
 * 按照顺序依次进行空闲检测，第一个空闲检测成功的机器被选定为目标执行器并发起调度；当执行器处于忙碌的状态时，则转移至不忙碌的机器.
 *
 * <p>
 * 实现原理：通过调用机器的idleBeat接口查看机器的返回状态来判定是否忙碌，如果处于忙碌或不可用状态则循环下一个继续该步骤，
 * 直到找到空闲且可用的机器或者没有可用机器为止.
 */
public class ExecutorRouteBusyover extends ExecutorRouter {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorRouteBusyover.class);

    /**
     * 忙碌转移路由策略.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行器地址列表
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
                ExecutorParam executorParam = new ExecutorParam();
                executorParam.setJobId(triggerParam.getJobId());
                // 空闲检测
                responseCode = handlerExecutor.idleBeat(executorParam);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            if (Objects.equals("00000000", responseCode)) {
                return address;
            }
        }
        // 没有空闲的执行器
        return null;
    }
}
