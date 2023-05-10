package com.alita.framework.job.core.thread;

import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.core.trigger.TriggerTypeEnum;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLog;
import com.alita.framework.job.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 运行失败监视器,主要失败发送邮箱,重试触发器
 */
public class JobFailMonitorHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);


    private Thread monitorThread = null;

    private volatile AtomicBoolean toStop = new AtomicBoolean(false);

    private static final MapperFactory mapperFactory = JobServerConfig.getMapperFactory();
    private static final JobServerConfig jobServerConfig = JobServerConfig.getJobServerConfig();

    /**
     * 静态内部类实现单例模式
     */
    private static class JobFailMonitorHelperInstance {
        private static final JobFailMonitorHelper instance = new JobFailMonitorHelper();
    }

    /**
     * 获取当前类的实例-单例模式
     *
     * @return JobRegistryHelper
     */
    public static JobFailMonitorHelper getInstance() {
        return JobFailMonitorHelperInstance.instance;
    }

    private JobFailMonitorHelper() {

    }

    public void start() {
        Runnable runnable = () -> {
            while (!toStop.get()) {
                try {
                    // 获取执行失败的日志，调度日志表：用于保存 JOB 任务调度的历史信息，如调度结果、执行结果、调度入参、调度机器和执行器等；
                    // 这里判断失败有2种情况(trigger_code表示调度中心调用执行器状态，handle_code表示执行器执行结束后回调给调度中心的状态，1：均标识成功，0：标识失败)
                    // 第一种：trigger_code!=1 且 handle_code!=1
                    // 第二种：handle_code!=1
                    // TODO 调度中心调用执行器状态和执行器执行结束的状态：0-失败；1-成功
                    // 查询 job_log 表1000条未处理的失败日志，条件为（trigger_code = 0 && handle_code = 0）or（handle_code = 0）
                    List<String> failJobLogIds = mapperFactory.getJobLogMapper().findFailJobLogIds(1000);
                    if (CollectionUtils.isNotEmpty(failJobLogIds)) {
                        for (String failJobLogId: failJobLogIds) {
                            int lockRet = mapperFactory.getJobLogMapper().updateAlarmStatus(failJobLogId, 0, -1);
                            if (lockRet < 1) {
                                continue;
                            }
                            // 获取失败日志的对象
                            JobLog jobLog = mapperFactory.getJobLogMapper().queryById(failJobLogId);
                            // 获取失败日志对应的 job 信息
                            JobInfo jobInfo = mapperFactory.getJobInfoMapper().queryById(jobLog.getJobId());
                            // 1、判断是否还有剩余失败重试次数，有则重试，并将次数-1（日志里存的失败重试次数实为剩余可重复次数）
                            if (jobLog.getExecutorFailRetryCount() > 0) {
                                // 触发任务执行
                                JobTriggerPoolHelper.trigger(
                                        jobLog.getJobId(),
                                        TriggerTypeEnum.RETRY,
                                        (int) jobLog.getExecutorFailRetryCount() -1,
                                        jobLog.getExecutorShardingParam(),
                                        jobLog.getExecutorParam(),
                                        null);
                                jobLog.setTriggerMsg(jobLog.getTriggerMsg());
                                mapperFactory.getJobLogMapper().updateTriggerInfo(jobLog);
                            }

                            // 2、失败告警
                            int newAlarmStatus = 0;  // 告警状态：0:默认、-1:锁定状态、1:无需告警、2:告警成功、3:告警失败
                            if (jobInfo != null) { //若设置报警邮箱，则执行报警
                                boolean alarmResult = jobServerConfig.getJobAlarmer().alarm(jobInfo, jobLog);
                                newAlarmStatus = alarmResult ? 2 : 3;
                            } else {
                                newAlarmStatus = 1; //没设置报警邮箱，则更改状态为不需要告警
                            }
                            // 更新告警状态
                            mapperFactory.getJobLogMapper().updateAlarmStatus(failJobLogId, -1, newAlarmStatus);
                        }
                    }
                } catch (Exception ex) {
                    if (!toStop.get()) {
                        logger.error(">>>>>>>>>>> job fail monitor thread error:{}", ex);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.info(">>>>>>>>>>> job fail monitor thread stop");
        };
        monitorThread = new Thread(runnable);
        monitorThread.setDaemon(true);
        monitorThread.setName("JobFailMonitorHelper");
        monitorThread.start();
    }


    public void toStop(){
        toStop.compareAndSet(false, true);
        // interrupt and wait
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
