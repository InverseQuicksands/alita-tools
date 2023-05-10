package com.alita.framework.job.core.scheduler;

import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.biz.client.JobHandlerClient;
import com.alita.framework.job.core.context.JobScheduleContext;
import com.alita.framework.job.core.thread.*;
import com.alita.framework.job.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Job 的核心类，负责初始化和销毁线程.
 */
public class JobScheduler implements JobScheduleContext {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private static final JobServerConfig jobServerConfig = JobServerConfig.getJobServerConfig();
    private static Map<String, JobHandlerExecutor> executorBizRepository = new ConcurrentHashMap<>();

    /**
     * 系统初始化.
     *
     * <p>用于初始化各个业务线程池和守护线程.
     */
    public void initialize() {

        // 初始化触发器线程池
        JobTriggerPoolHelper.getInstance().start();

        /**
         * 主要作用：开启一个守护线程，每隔30s扫描一次执行器的注册信息表
         * 1、剔除90s内没有进行健康检查的执行器信息
         * 2、将自动注册类型的执行器注册信息（Job_Registry）经过处理更新执行器信息（Job_Group）
         */
        JobRegistryHelper.getInstance().start();

        /**
         * 主要作用：开启一个守护线程，10s扫描一次失败日志
         * 1、如果任务失败可重试次数>0，那么重新触发任务
         * 2、如果任务执行失败，会进行告警，默认采用邮件形式进行告警
         */
        JobFailMonitorHelper.getInstance().start();

        // 将丢失主机信息调度日志更改状态
        JobCompleteHelper.getInstance().start();

        /**
         * 主要作用：开通一个守护线程，每隔1min扫描一次最近3天的调度日志
         * 1、更新每天总任务数、正在执行数、执行成功数、执行失败数
         */
        JobLogReportHelper.getInstance().start();

        /**
         * 主要作用：
         * 1、开通一个守护线程，每隔5s扫表一次执行器的任务表
         *   1.1 执行（已到执行时间）的任务，并更新任务的下次执行时间
         *
         * 2、开通一个守护线程，每隔1s循环执行一次待执行的任务
         *  2.1 避免任务执行遗漏
         */
        JobScheduleHelper.getInstance().start();

        logger.info(">>>>>>>>> init job admin success.");
    }

    /**
     * 业务线程池销毁.
     *
     * <p>应用关闭时销毁各个线程池.
     */
    public void destroy() {
        // stop-schedule
        JobScheduleHelper.getInstance().toStop();

        // admin log report stop
        JobLogReportHelper.getInstance().toStop();

        // admin lose-monitor stop
        JobCompleteHelper.getInstance().toStop();

        // admin fail-monitor stop
        JobFailMonitorHelper.getInstance().toStop();

        // admin registry stop
        JobRegistryHelper.getInstance().toStop();

        // admin trigger pool stop
        JobTriggerPoolHelper.getInstance().stop();
    }


    /**
     * 获取执行器客户端.
     *
     * @param address 地址
     * @return 执行器客户端
     * @throws Exception
     */
    public static JobHandlerExecutor getJobHandlerExecutor(String address) throws Exception {
        if (StringUtils.isBlank(address)) {
            return null;
        }

        // load-cache
        address = address.trim();
        JobHandlerExecutor handlerExecutor = executorBizRepository.get(address);
        if (handlerExecutor != null) {
            return handlerExecutor;
        }

        // set-cache
        handlerExecutor = new JobHandlerClient(address);
        executorBizRepository.put(address, handlerExecutor);
        return handlerExecutor;
    }


}
