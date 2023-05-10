package com.alita.framework.job.core.thread;

import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.scheduler.JobTrigger;
import com.alita.framework.job.core.trigger.TriggerTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Job 触发器线程池。
 *
 * <p>
 * 作为触发器调用的统一入口，为触发器的调用提供线程池异步处理，并根据触发时间进行线程池的区分。
 * 共有四个地方会调用触发器：<br>
 * <ol>
 * <li>调度器触发执行：由定时任务的触发器正常调度（JobScheduleHelper）</li>
 * <li>手动触发执行：从任务信息页面，点击执行一次，手动触发执行（JobInfoController）</li>
 * <li>失败监听器触发执行：如果任务执行失败，并且任务设置了失败重试次数，会根据重试次数再次调用触发器执行（JobFailMonitorHelper）</li>
 * <li>父任务成功触发执行：设置了父子任务的情况下，父任务成功后，会由JobCompleter 触发调用子任务的触发器执行（JobCompleter）</li>
 * </ol>
 *
 */
public class JobTriggerPoolHelper {

    public static final Logger logger = LoggerFactory.getLogger(JobTriggerPoolHelper.class);

    /**
     * 快线程池
     */
    private ThreadPoolExecutor fastTriggerPool = null;

    /**
     * 慢线程池
     */
    private ThreadPoolExecutor slowTriggerPool = null;

    private static final JobServerConfig jobServerConfig = JobServerConfig.getJobServerConfig();

    /**
     * 静态内部类实现单例模式
     */
    private static class JobTriggerPoolHelperInstance {
        private static final JobTriggerPoolHelper instance = new JobTriggerPoolHelper();
    }

    /**
     * 获取当前类的实例-单例模式
     *
     * @return JobTriggerPoolHelper
     */
    public static JobTriggerPoolHelper getInstance() {
        return JobTriggerPoolHelperInstance.instance;
    }

    private JobTriggerPoolHelper() {

    }

    /**
     * 这里分别初始化了2个线程池，一个快一个慢，优先选择快，当一分钟以内任务超过10次执行时间超过500ms，则加入慢线程池执行。
     */
    public void start() {
        // 快线程池
        NamedThreadFactory fastTriggerThreadFactory = new NamedThreadFactory("JobTriggerPoolHelper-fastTriggerPool", false);
        fastTriggerPool = new JobThreadExecutorBuilder()
                .setCorePoolSize(10)
                .setMaxPoolSize(jobServerConfig.getTriggerPoolFastMax())
                .setKeepAliveTime(60L, TimeUnit.SECONDS)
                .setThreadFactory(fastTriggerThreadFactory)
                .setWorkQueue(new LinkedBlockingQueue<>(1024))
                .build();

        // 慢线程池
        NamedThreadFactory slowTriggerThreadFactory = new NamedThreadFactory("JobTriggerPoolHelper-slowTriggerPool", false);
        slowTriggerPool = new JobThreadExecutorBuilder()
                .setCorePoolSize(10)
                .setMaxPoolSize(jobServerConfig.getTriggerPoolSlowMax())
                .setKeepAliveTime(60L, TimeUnit.SECONDS)
                .setThreadFactory(slowTriggerThreadFactory)
                .setWorkQueue(new LinkedBlockingQueue<>(2048))
                .build();
    }


    public void stop() {
        fastTriggerPool.shutdown();
        slowTriggerPool.shutdown();
        logger.info(">>>>>>>>> job trigger thread pool shutdown success.");
    }


    // job timeout count
    private volatile long minTim = System.currentTimeMillis() / 60000;     // ms > min

    // job 超时统计
    private volatile ConcurrentMap<String, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();


    public void addTrigger(final String jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorShardingParam,
                           final String executorParam,
                           final String addressList) {

        // 选择线程池
        ThreadPoolExecutor triggerPool = fastTriggerPool;
        AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);
        // job-timeout 10 times in 1 min（如果在一分钟内超时超过10次，则放入慢线程中）
        if (jobTimeoutCount!=null && jobTimeoutCount.get() > 10) {
            triggerPool = slowTriggerPool;
        }

        Runnable runnable = () -> {
            long start = System.currentTimeMillis();
            try {
                JobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                // check timeout-count-map
                // 检查时间循环，每整分钟清空一次超时触发集合
                long minTim_now = System.currentTimeMillis()/60000;
                if (minTim != minTim_now) {
                    minTim = minTim_now;
                    jobTimeoutCountMap.clear();
                }

                // incr timeout-count-map
                long cost = System.currentTimeMillis() - start;
                if (cost > 500) {       // ob-timeout threshold 500ms
                    // 根据JobId进行统计, 任务触发超过500ms认为超时, 统计超时次数
                    AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                    if (timeoutCount != null) {
                        timeoutCount.incrementAndGet();
                    }
                }
            }
        };

        triggerPool.execute(runnable);
    }



    /**
     * @param jobId
     * @param triggerType
     * @param failRetryCount
     * 			>=0: use this param
     * 			{@code <0: use param from job info config}
     * @param executorShardingParam
     * @param executorParam
     *          null: use job param
     *          not null: cover job param
     */
    public static void trigger(String jobId, TriggerTypeEnum triggerType, int failRetryCount,
                               String executorShardingParam, String executorParam,
                               String addressList) {

        JobTriggerPoolHelperInstance.instance.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
    }

}
