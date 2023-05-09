package com.alita.framework.job.core.thread;

import com.alita.framework.job.common.SnowFlakeId;
import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.model.JobLogReport;
import com.alita.framework.job.utils.DateUtils;
import com.alita.framework.job.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 调度日志
 */
public class JobLogReportHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobLogReportHelper.class);


    private static final MapperFactory mapperFactory = JobServerConfig.getMapperFactory();
    private static final JobServerConfig jobServerConfig = JobServerConfig.getJobServerConfig();
    private Thread logrThread;
    private volatile AtomicBoolean toStop = new AtomicBoolean(false);

    /**
     * 静态内部类实现单例模式
     */
    private static class JobLogReportHelperInstance {
        private static final JobLogReportHelper instance = new JobLogReportHelper();
    }

    /**
     * 获取当前类的实例-单例模式
     *
     * @return JobRegistryHelper
     */
    public static JobLogReportHelper getInstance() {
        return JobLogReportHelperInstance.instance;
    }

    private JobLogReportHelper() {

    }


    public void start() {
        Runnable runnable = () -> {
            // last clean log time
            long lastCleanLogTime = 0;

            while(!toStop.get()) {
                // 1、log-report refresh: refresh log report in 3 days
                try {
                    for (int i = 0; i < 3; i++) {
                        // today  分别统计今天,昨天,前天0~24点的数据
                        Calendar itemDay = Calendar.getInstance();
                        itemDay.add(Calendar.DAY_OF_MONTH, -i);
                        itemDay.set(Calendar.HOUR_OF_DAY, 0);
                        itemDay.set(Calendar.MINUTE, 0);
                        itemDay.set(Calendar.SECOND, 0);
                        itemDay.set(Calendar.MILLISECOND, 0);
                        itemDay.set(Calendar.HOUR_OF_DAY, 23);
                        itemDay.set(Calendar.MINUTE, 59);
                        itemDay.set(Calendar.SECOND, 59);
                        itemDay.set(Calendar.MILLISECOND, 999);

                        Date todayFrom = itemDay.getTime();
                        Date todayTo = itemDay.getTime();

                        // refresh log-report every minute
                        // 设置默认值
                        JobLogReport jobLogReport = new JobLogReport();
                        jobLogReport.setTriggerDay(todayFrom);
                        jobLogReport.setRunningCount(0);
                        jobLogReport.setSucCount(0);
                        jobLogReport.setFailCount(0);

                        // 查询失败, 成功，总的调用次数
                        Map<String, Object> triggerCountMap = mapperFactory.getJobLogMapper().findLogReport(todayFrom, todayTo);
                        if (MapUtils.isNotEmpty(triggerCountMap)) {
                            int triggerDayCount = triggerCountMap.containsKey("triggerDayCount")?Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCount"))):0;
                            int triggerDayCountRunning = triggerCountMap.containsKey("triggerDayCountRunning")?Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCountRunning"))):0;
                            int triggerDayCountSuc = triggerCountMap.containsKey("triggerDayCountSuc")?Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCountSuc"))):0;
                            int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                            jobLogReport.setRunningCount(triggerDayCountRunning);
                            jobLogReport.setSucCount(triggerDayCountSuc);
                            jobLogReport.setFailCount(triggerDayCountFail);
                            jobLogReport.setId(String.valueOf(SnowFlakeId.getInstance().generateId48()));
                        }

                        // do refresh
                        // 刷新调用次数,若找不到则默认都是0
                        int ret = mapperFactory.getJobLogReportMapper().update(jobLogReport);
                        if (ret < 1) {
                            mapperFactory.getJobLogReportMapper().save(jobLogReport);
                        }
                    }

                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(">>>>>>>>>>> job log report thread error:{}", e);
                    }
                }

                // 2、log-clean: switch open & once each day
                // 设置了保留日志天数且日志保留了24小时，则进入
                if (jobServerConfig.getLogretentiondays()>0
                        && System.currentTimeMillis() - lastCleanLogTime > 24*60*60*1000) {

                    // expire-time
                    // 通过日志保留天数算出清除log时间
                    Calendar expiredDay = Calendar.getInstance();
                    expiredDay.add(Calendar.DAY_OF_MONTH, -1 * jobServerConfig.getLogretentiondays());
                    expiredDay.set(Calendar.HOUR_OF_DAY, 0);
                    expiredDay.set(Calendar.MINUTE, 0);
                    expiredDay.set(Calendar.SECOND, 0);
                    expiredDay.set(Calendar.MILLISECOND, 0);
                    Date clearBeforeTime = expiredDay.getTime();

                    // clean expired log
                    // 这里传了3个0表示查询所有,而不是单个任务id
                    List<Long> logIds = null;
                    do {
                        logIds = mapperFactory.getJobLogMapper().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
                        // 删除过期数据
                        if (logIds!=null && logIds.size()>0) {
                            mapperFactory.getJobLogMapper().clearLog(logIds);
                        }
                    } while (logIds!=null && logIds.size()>0);

                    // update clean time
                    lastCleanLogTime = System.currentTimeMillis();
                }

                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }

            logger.info(">>>>>>>>>>> job log report thread stop");
        };

        logrThread = new Thread(runnable);
        logrThread.setDaemon(true);
        logrThread.setName("JobLogReportHelper");
        logrThread.start();
    }


    public void toStop(){
        toStop.compareAndSet(false, true);
        // interrupt and wait
        logrThread.interrupt();
        try {
            logrThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
