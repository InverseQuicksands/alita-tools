package com.alita.framework.job.core.thread;

import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.core.scheduler.JobCompleter;
import com.alita.framework.job.model.JobLog;
import com.alita.framework.job.utils.CollectionUtils;
import com.alita.framework.job.utils.DateUtils;
import com.alita.framework.job.utils.I18nUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 处理任务结果丢失，记录 job_logv表
 */
public class JobCompleteHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobCompleteHelper.class);

    private static final MapperFactory mapperFactory = JobServerConfig.getMapperFactory();
    private static final JobServerConfig jobServerConfig = JobServerConfig.getJobServerConfig();

    private ThreadPoolExecutor callbackThreadPool = null;
    private Thread monitorThread;
    private volatile AtomicBoolean toStop = new AtomicBoolean(false);

    /**
     * 静态内部类实现单例模式
     */
    private static class JobCompleteHelperInstance {
        private static final JobCompleteHelper instance = new JobCompleteHelper();
    }

    /**
     * 获取当前类的实例-单例模式
     *
     * @return JobRegistryHelper
     */
    public static JobCompleteHelper getInstance() {
        return JobCompleteHelperInstance.instance;
    }

    private JobCompleteHelper() {

    }


    public void start() {
        // 构造一个 callbackThreadPool 线程池，用来更新 job_log 记录的执行结果
        NamedThreadFactory callbackThreadFactory = new NamedThreadFactory("JobCompleteHelper-callbackThreadPool", false);
        callbackThreadPool = new JobThreadExecutorBuilder()
                .setCorePoolSize(2)
                .setMaxPoolSize(20)
                .setKeepAliveTime(30L, TimeUnit.SECONDS)
                .setWorkQueue(new LinkedBlockingQueue<>(3000))
                .setThreadFactory(callbackThreadFactory)
                .setHandler((Runnable runnable, ThreadPoolExecutor executor) -> {
                    // 这里的拒绝策略是再次执行
                    runnable.run();
                    logger.warn(">>>>>>>>>>> callback too fast, match threadpool rejected handler(run now).");
                })
                .build();

        // 构造一个 monitorThread 线程，处理执行超时的
        Runnable runnable = () -> {
            // wait for JobTriggerPoolHelper-init
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                if (!toStop.get()) {
                    logger.error(e.getMessage(), e);
                }
            }

            // monitor
            while (!toStop.get()) {
                try {
                    // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
                    // TODO 调度中心调用执行器状态和执行器执行结束的状态：99999999-失败；00000000-成功
                    Date losedTime = DateUtils.minusMinutes(new Date(), 10);
                    List<String> losedJobIds = mapperFactory.getJobLogMapper().findLostJobIds(losedTime);
                    if (CollectionUtils.isNotEmpty(losedJobIds)) {
                        for (String logId: losedJobIds) {
                            JobLog jobLog = new JobLog();
                            jobLog.setId(logId);
                            jobLog.setHandleTime(new Date());
                            jobLog.setHandleCode("99999999"); // 失败
                            jobLog.setHandleMsg(I18nUtils.getProperty("joblog_lost_fail"));

                            JobCompleter.updateHandleInfoAndFinish(jobLog);
                        }
                    }
                } catch (Exception ex) {
                    if (!toStop.get()) {
                        logger.error(">>>>>>>>>>> job fail monitor thread error:{}", ex);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(60);
                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            logger.info(">>>>>>>>>>> JobCompleteHelper stop");
        };
        monitorThread = new Thread(runnable);
        monitorThread.setDaemon(true);
        monitorThread.setName("JobCompleteHelper");
        monitorThread.start();
    }


    public void toStop(){
        toStop.compareAndSet(false, true);

        // stop registryOrRemoveThreadPool
        callbackThreadPool.shutdownNow();

        // stop monitorThread (interrupt and wait)
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    public void callback(List<HandleCallbackParam> callbackParamList) {
        Runnable runnable = () -> {
            for (HandleCallbackParam handleCallbackParam: callbackParamList) {
                String callbackResult = callback(handleCallbackParam);
                logger.debug(">>>>>>>>> handleCallbackParam={}, callbackResult={}",handleCallbackParam, callbackResult);
            }
        };
        callbackThreadPool.execute(runnable);
    }

    private String callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLog log = mapperFactory.getJobLogMapper().queryById(String.valueOf(handleCallbackParam.getLogId()));
        if (log == null) {
            return "log item not found.";
        }
        if (log.getHandleCode().equals("00000000")) {
            return "log repeate callback.";     // avoid repeat callback, trigger child job etc
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() !=null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getHandleMsg() != null) {
            handleMsg.append(handleCallbackParam.getHandleMsg());
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getHandleCode());
        log.setHandleMsg(handleMsg.toString());
        JobCompleter.updateHandleInfoAndFinish(log);

        return "00000000";
    }


}
