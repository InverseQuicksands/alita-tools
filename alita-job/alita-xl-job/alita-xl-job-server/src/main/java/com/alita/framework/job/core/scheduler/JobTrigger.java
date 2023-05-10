package com.alita.framework.job.core.scheduler;

import com.alita.framework.job.common.SnowFlakeId;
import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.biz.model.TriggerParam;
import com.alita.framework.job.core.enums.ExecutorBlockStrategyEnum;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.core.route.ExecutorRouteStrategyEnum;
import com.alita.framework.job.core.trigger.TriggerTypeEnum;
import com.alita.framework.job.model.JobGroup;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLog;
import com.alita.framework.job.utils.CollectionUtils;
import com.alita.framework.job.utils.I18nUtils;
import com.alita.framework.job.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 任务触发器.
 */
public class JobTrigger {

    public static final Logger logger = LoggerFactory.getLogger(JobTrigger.class);

    private static final MapperFactory mapperFactory = JobServerConfig.getMapperFactory();

    /**
     * trigger job
     *
     * @param jobId
     * @param triggerType
     * @param failRetryCount
     * 			>=0: use this param
     * 			{@code <0: use param from job info config}
     * @param executorShardingParam
     * @param executorParam
     *          null: use job param
     *          not null: cover job param
     * @param addressStr
     *          null: use executor addressList
     *          not null: cover
     */
    public static void trigger(String jobId,
                               TriggerTypeEnum triggerType,
                               int failRetryCount,
                               String executorShardingParam,
                               String executorParam,
                               String addressStr) {

        // 获取 job 信息
        JobInfo jobInfo = mapperFactory.getJobInfoMapper().queryById(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        // 如果执行参数不为空则赋值
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        // 获取 job 失败重试次数
        int executorFailRetryCount = jobInfo.getExecutorFailRetryCount();
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : executorFailRetryCount;
        // 获取 job 分组信息
        JobGroup jobGroup = mapperFactory.getJobGroupMapper().queryById(jobInfo.getJobGroup());

        // cover addressList
        if (StringUtils.isNoneBlank(addressStr)) {
            jobGroup.setAddressType(1);
            jobGroup.setAddressList(addressStr.trim());
        }

        // sharding param（分片参数）
        int[] shardingParam = null;
        if (executorShardingParam != null){
            // why split with '/' ？
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.valueOf(shardingArr[0]);
                shardingParam[1] = Integer.valueOf(shardingArr[1]);
            }
        }
        // 处理路由策略
        boolean routeStrategy = ExecutorRouteStrategyEnum.SHARDING_BROADCAST == ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        List<String> registryList = jobGroup.getRegistryList();
        if (routeStrategy && CollectionUtils.isNotEmpty(registryList) && shardingParam==null) {
            for (int i = 0; i < registryList.size(); i++) {
                processTrigger(jobGroup, jobInfo, finalFailRetryCount, triggerType, i, registryList.size());
            }
        } else {
            if (shardingParam == null) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(jobGroup, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }
    }

    /**
     * 判断是否是数字
     *
     * @param str 字符串
     * @return boolean
     */
    private static boolean isNumeric(String str){
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * 执行
     *
     * @param jobGroup 信息
     * @param jobInfo 信息
     * @param finalFailRetryCount 重试次数
     * @param triggerType 触发类型
     * @param index 分片参数
     * @param total 分片参数
     */
    private static void processTrigger(JobGroup jobGroup, JobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {
        // 执行器阻塞策略：调度过于密集执行器来不及处理时的处理策略，策略包括：单机串行（默认）、丢弃后续调度、覆盖之前调度；
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        // 执行器路由策略：执行器集群部署时提供丰富的路由策略，包括：第一个、最后一个、轮询、随机、一致性HASH、最不经常使用、最近最久未使用、故障转移、忙碌转移等
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        // 处理分片参数：如果为分片广播则将index和total使用/进行拼接
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST==executorRouteStrategyEnum)?String.valueOf(index).concat("/").concat(String.valueOf(total)):null;

        // 1、保存 jobLog
        JobLog jobLog = new JobLog();
        jobLog.setId(String.valueOf(SnowFlakeId.getInstance().generateId48()));
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        jobLog.setHandleCode("00000000");
        mapperFactory.getJobLogMapper().save(jobLog);
        logger.debug(">>>>>>>>>>> job trigger start, jobId:{}", jobLog.getId());

        // 2、初始化 trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(Long.valueOf(jobLog.getId()));
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、init address
        String address = null;
        String routeAddressResult = null;
        if (CollectionUtils.isNotEmpty(jobGroup.getRegistryList())) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < jobGroup.getRegistryList().size()) {
                    address = jobGroup.getRegistryList().get(index);
                } else {
                    address = jobGroup.getRegistryList().get(0);
                }
            } else {
                address = executorRouteStrategyEnum.getRouter().route(triggerParam, jobGroup.getRegistryList());
            }
        }

        // 4、trigger remote executor
        long triggerResult;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address); // 1-成功
        } else {
            triggerResult = 0; // 失败
        }

        // 5、collection trigger info
        StringBuffer triggerMessage = new StringBuffer();
        triggerMessage.append(I18nUtils.getProperty("jobconf_trigger_type"))
                .append("：")
                .append(triggerType.getTitle());

        triggerMessage.append("<br>")
                .append(I18nUtils.getProperty("jobconf_trigger_admin_adress"))
                .append("：")
                .append(IpUtils.getIp());

        triggerMessage.append("<br>")
                .append(I18nUtils.getProperty("jobconf_trigger_exe_regtype"))
                .append("：")
                .append((jobGroup.getAddressType() == 0)?I18nUtils.getProperty("jobgroup_field_addressType_0"):I18nUtils.getProperty("jobgroup_field_addressType_1"));

        triggerMessage.append("<br>")
                .append(I18nUtils.getProperty("jobconf_trigger_exe_regaddress"))
                .append("：")
                .append(jobGroup.getRegistryList());

        triggerMessage.append("<br>")
                .append(I18nUtils.getProperty("jobinfo_field_executorRouteStrategy"))
                .append("：")
                .append(executorRouteStrategyEnum.getTitle());

        if (shardingParam != null) {
            triggerMessage.append("("+shardingParam+")");
        }

        triggerMessage.append("<br>")
                .append(I18nUtils.getProperty("jobinfo_field_executorBlockStrategy"))
                .append("：")
                .append(blockStrategy.getTitle());

        triggerMessage.append("<br>")
                .append(I18nUtils.getProperty("jobinfo_field_timeout"))
                .append("：")
                .append(jobInfo.getExecutorTimeout());

        triggerMessage.append("<br>")
                .append(I18nUtils.getProperty("jobinfo_field_executorFailRetryCount"))
                .append("：")
                .append(finalFailRetryCount);

        // 6、save log trigger-info
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        jobLog.setTriggerCode(triggerResult);
        if (triggerResult == 0) {
            jobLog.setHandleCode("99999999");
        }
        jobLog.setTriggerMsg(triggerMessage.toString());

        mapperFactory.getJobLogMapper().updateTriggerInfo(jobLog);

        logger.debug(">>>>>>>>>>> job trigger end, jobId:{}", jobLog.getId());
    }


    /**
     * run
     *
     * @param triggerParam 执行参数
     * @param address 地址
     * @return 执行结果：1-成功；0-失败
     */
    public static long runExecutor(TriggerParam triggerParam, String address){
        long runResult = 1;
        try {
            JobHandlerExecutor handlerExecutor = JobScheduler.getJobHandlerExecutor(address);
            String result = handlerExecutor.run(triggerParam);
            if ("00000000".equals(result)) {
                runResult = 1;
            } else if ("99999999".equals(result)) {
                runResult = 0;
            }
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = 0;
        }

        return runResult;
    }

}
