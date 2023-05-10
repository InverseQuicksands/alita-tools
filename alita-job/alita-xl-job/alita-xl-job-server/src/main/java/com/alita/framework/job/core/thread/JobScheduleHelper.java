package com.alita.framework.job.core.thread;

import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.core.cron.CronExpression;
import com.alita.framework.job.core.scheduler.MisfireStrategyEnum;
import com.alita.framework.job.core.scheduler.ScheduleTypeEnum;
import com.alita.framework.job.core.trigger.TriggerTypeEnum;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class JobScheduleHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduleHelper.class);

    /**
     * 静态内部类实现单例模式
     */
    private static class JobScheduleHelperInstance {
        private static JobScheduleHelper instance = new JobScheduleHelper();
    }

    /**
     * 获取当前类的实例-单例模式
     *
     * @return JobScheduleHelper
     */
    public static JobScheduleHelper getInstance(){
        return JobScheduleHelperInstance.instance;
    }

    private JobScheduleHelper() {

    }

    /**
     * 5000 ms 预读取时间
     */
    public static final long PRE_READ_MS = 5000;
    private final MapperFactory mapperFactory = JobServerConfig.getMapperFactory();
    private final JobServerConfig jobServerConfig = JobServerConfig.getJobServerConfig();

    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private volatile static Map<Integer, List<Integer>> ringData = new ConcurrentHashMap<>();


    public void start() {
        Runnable runnable = () -> {
            try {
                // 保证5秒执行一次
                TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis() % 1000);
            } catch (InterruptedException e) {
                if (!scheduleThreadToStop) {
                    logger.error(e.getMessage(), e);
                }
            }

            logger.info(">>>>>>>>> init job admin scheduler success.");

            // pre-read count: readpool-size * trigger-qps (each trigger cost 50ms, qps = 1000/50 = 20)
            // 每个触发器花费50ms,每个线程单位时间内处理20任务,最多同时处理300*20=6000任务
            int preReadCount = (jobServerConfig.getTriggerPoolFastMax() + jobServerConfig.getTriggerPoolSlowMax()) * 20;

            while (!scheduleThreadToStop) {
                long start = System.currentTimeMillis();

                Connection connection = null;
                Boolean connAutoCommit = null;
                PreparedStatement preparedStatement = null;

                boolean preReadSuc = true;
                // 通过行级锁（悲观锁）来控制不同实例同时竞争
                // MySQL的'select for update'： 要有索引for update才是行级锁，否则就是全表锁.
                // PostgreSQL的'select for update': 只会锁select for update那部分的结果.
                try {
                    connection = jobServerConfig.getDataSource().getConnection();
                    connection.setAutoCommit(false);
                    connAutoCommit = connection.getAutoCommit();
                    // 获取任务调度锁表内数据信息,加写锁
                    String sql = "select * from job_lock where lock_name = 'schedule_lock' for update";
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.execute();

                    // 时间轮实现
                    // 1、pre read
                    long nowTime = System.currentTimeMillis();
                    // 获取当前时间后5秒,同时最多负载的分页数
                    List<JobInfo> scheduleList = mapperFactory.getJobInfoMapper().scheduleJobQuery(nowTime + PRE_READ_MS, preReadCount);
                    if (CollectionUtils.isNotEmpty(scheduleList)) {
                        // 2、push time-ring
                        for (JobInfo jobInfo : scheduleList) {
                            // time-ring jump
                            // 如果当前时间 > （下次调度时间 + 预读取时间）
                            // 触发器过期时间>5s
                            if (nowTime > Long.valueOf(jobInfo.getTriggerNextTime())  + PRE_READ_MS) {
                                // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                                // 如果触发执行到期时间 > 5s：则通过并且设置下次触发时间
                                logger.warn(">>>>>>>>>>> job schedule misfire, jobId = " + jobInfo.getId());
                                // 1、misfire match
                                //- 调度过期策略：
                                //- 忽略：调度过期后，忽略过期的任务，从当前时间开始重新计算下次触发时间；
                                //- 立即执行一次：调度过期后，立即执行一次，并从当前时间开始重新计算下次触发时间；
                                MisfireStrategyEnum misfireStrategyEnum = MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), MisfireStrategyEnum.DO_NOTHING);
                                if (MisfireStrategyEnum.FIRE_ONCE_NOW == misfireStrategyEnum) {
                                    // FIRE_ONCE_NOW 》 trigger
                                    JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.MISFIRE, -1, null, null, null);
                                    logger.trace(">>>>>>>>>>> job schedule push trigger : jobId = " + jobInfo.getId() );
                                }
                                // 2、fresh next
                                refreshNextValidTime(jobInfo, new Date());
                            } else if (nowTime > Long.valueOf(jobInfo.getTriggerNextTime())) {
                                // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                                // 1、trigger
                                JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                                logger.trace(">>>>>>>>>>> job schedule push trigger : jobId = " + jobInfo.getId() );

                                // 2、fresh next
                                refreshNextValidTime(jobInfo, new Date());

                                // next-trigger-time in 5s, pre-read again
                                if (Integer.valueOf(jobInfo.getTriggerStatus())==1 && nowTime + PRE_READ_MS > Long.valueOf(jobInfo.getTriggerNextTime())) {

                                    // 1、make ring second
                                    int ringSecond = (int)((Long.valueOf(jobInfo.getTriggerNextTime())/1000)%60);

                                    // 2、push time ring
                                    pushTimeRing(ringSecond, Integer.valueOf(jobInfo.getId()));

                                    // 3、fresh next
                                    refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                                }
                            } else {
                                // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time

                                // 1、make ring second
                                int ringSecond = (int)((Long.valueOf(jobInfo.getTriggerNextTime())/1000)%60);

                                // 2、push time ring
                                pushTimeRing(ringSecond, Integer.valueOf(jobInfo.getId()));

                                // 3、fresh next
                                refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                            }
                        }

                        // 3、update trigger info
                        for (JobInfo jobInfo: scheduleList) {
                            mapperFactory.getJobInfoMapper().scheduleUpdate(jobInfo);
                        }
                    } else {
                        preReadSuc = false;
                    }


                } catch (Exception e) {
                    if (!scheduleThreadToStop) {
                        logger.error(">>>>>>>>>>> job JobScheduleHelper#scheduleThread error:{}", e);
                    }
                } finally {
                    // commit
                    if (connection != null) {
                        try {
                            connection.commit();
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                        try {
                            connection.setAutoCommit(connAutoCommit);
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }

                    // close PreparedStatement
                    if (null != preparedStatement) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }

                long cost = System.currentTimeMillis()-start;


                // Wait seconds, align second
                if (cost < 1000) {  // scan-overtime, not wait
                    try {
                        // pre-read period: success > scan each second; fail > skip this period;
                        TimeUnit.MILLISECONDS.sleep((preReadSuc?1000:PRE_READ_MS) - System.currentTimeMillis()%1000);
                    } catch (InterruptedException e) {
                        if (!scheduleThreadToStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
            logger.info(">>>>>>>>>>> job JobScheduleHelper#scheduleThread stop");
        };

        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("JobScheduleHelper-scheduleThread");
        thread.start();

        // ring thread
        ringThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!ringThreadToStop) {

                    // align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                    } catch (InterruptedException e) {
                        if (!ringThreadToStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    try {
                        // second data
                        List<Integer> ringItemData = new ArrayList<>();
                        int nowSecond = Calendar.getInstance().get(Calendar.SECOND);   // 避免处理耗时太长，跨过刻度，向前校验一个刻度；
                        for (int i = 0; i < 2; i++) {
                            List<Integer> tmpData = ringData.remove( (nowSecond+60-i)%60 );
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }
                        }

                        // ring trigger
                        logger.trace(">>>>>>>>>>> job time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData) );
                        if (ringItemData.size() > 0) {
                            // do trigger
                            for (int jobId: ringItemData) {
                                // do trigger
                                JobTriggerPoolHelper.trigger(String.valueOf(jobId), TriggerTypeEnum.CRON, -1, null, null, null);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (Exception e) {
                        if (!ringThreadToStop) {
                            logger.error(">>>>>>>>>>> job JobScheduleHelper#ringThread error:{}", e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> job JobScheduleHelper#ringThread stop");
            }
        });
        ringThread.setDaemon(true);
        ringThread.setName("JobScheduleHelper-ringThread");
        ringThread.start();
    }


    private void refreshNextValidTime(JobInfo jobInfo, Date fromTime) throws Exception {
        Date nextValidTime = generateNextValidTime(jobInfo, fromTime);
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime.getTime());
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0);
            jobInfo.setTriggerNextTime(0);
            logger.warn(">>>>>>>>>>> job refreshNextValidTime fail for job: jobId={}, scheduleType={}, scheduleConf={}",
                    jobInfo.getId(), jobInfo.getScheduleType(), jobInfo.getScheduleConf());
        }
    }

    private void pushTimeRing(int ringSecond, int jobId){
        // push async ring
        List<Integer> ringItemData = ringData.get(ringSecond);
        if (ringItemData == null) {
            ringItemData = new ArrayList<Integer>();
            ringData.put(ringSecond, ringItemData);
        }
        ringItemData.add(jobId);

        logger.debug(">>>>>>>>>>> job schedule push time-ring : " + ringSecond + " = " + Arrays.asList(ringItemData) );
    }



    /**
     * 生成下次执行时间
     *
     * @param jobInfo job信息
     * @param fromTime 时间
     * @return 时间
     * @throws Exception
     */
    public static Date generateNextValidTime(JobInfo jobInfo, Date fromTime) throws Exception {
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        if (ScheduleTypeEnum.CRON == scheduleTypeEnum) {
            Date nextValidTime = new CronExpression(jobInfo.getScheduleConf()).getNextValidTimeAfter(fromTime);
            return nextValidTime;
        } else if (ScheduleTypeEnum.FIX_RATE == scheduleTypeEnum) {
            return new Date(fromTime.getTime() + Integer.valueOf(jobInfo.getScheduleConf()) * 1000 );
        }
        return null;
    }


    public void toStop(){

        // 1、stop schedule
        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);  // wait
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (scheduleThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            scheduleThread.interrupt();
            try {
                scheduleThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        // if has ring data
        boolean hasRingData = false;
        if (!ringData.isEmpty()) {
            for (int second : ringData.keySet()) {
                List<Integer> tmpData = ringData.get(second);
                if (tmpData!=null && tmpData.size()>0) {
                    hasRingData = true;
                    break;
                }
            }
        }
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        // stop ring (wait job-in-memory stop)
        ringThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (ringThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            ringThread.interrupt();
            try {
                ringThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        logger.info(">>>>>>>>>>> job JobScheduleHelper stop");
    }

}
