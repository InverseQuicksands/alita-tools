package com.alita.framework.job.core.thread;


import com.alita.framework.job.common.Response;
import com.alita.framework.job.common.SnowFlakeId;
import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.biz.model.RegistryParam;
import com.alita.framework.job.core.enums.RegistryConfig;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.model.JobGroup;
import com.alita.framework.job.model.JobRegistry;
import com.alita.framework.job.utils.CollectionUtils;
import com.alita.framework.job.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 维护job注册列表信息，30s执行一次
 */
public class JobRegistryHelper {

    public static final Logger logger = LoggerFactory.getLogger(JobRegistryHelper.class);

    /**
     * 注册或者删除job线程池
     */
    private ThreadPoolExecutor registryOrRemoveThreadPool = null;

    /**
     * 维护注册信息线程
     */
    private Thread registryMonitorThread = null;

    private volatile AtomicBoolean toStop = new AtomicBoolean(false);

    private static final MapperFactory mapperFactory = JobServerConfig.getMapperFactory();

    /**
     * 静态内部类实现单例模式
     */
    private static class JobRegistryHelperInstance {
        private static final JobRegistryHelper instance = new JobRegistryHelper();
    }

    /**
     * 获取当前类的实例-单例模式
     *
     * @return JobRegistryHelper
     */
    public static JobRegistryHelper getInstance() {
        return JobRegistryHelperInstance.instance;
    }

    private JobRegistryHelper() {

    }


    /**
     * 维护注册表信息.
     *
     * <p>注册中心的检测线程具体工作流程如下: <br>
     * 1.注册中心检测线程的周期为默认心跳时间30s <br>
     * 2.第一步从数据库中job_group表中查询 adress_type=0 的所有组（0-自动注册;1-手动注册）<br>
     * 3.找到之后，继续查询是否有心跳超时的注册的执行器 <br>
     * 4.若找到超时的执行器节点，则剔除该节点，即执行数据库删除操作 <br>
     * 5.第二步从数据库中查找正常的注册器节点，组装出一个 appAddressMap  key->appName value->注册器注册节点地址List集合 <br>
     * 6.最后更新job_group表中address_list字段 <br>
     */
    public void start() {
        // （初始化注册或者删除job）线程池
        NamedThreadFactory registryOrRemoveThreadFactory = new NamedThreadFactory("JobRegistryHelper-registryOrRemoveThreadPool", false);
        registryOrRemoveThreadPool = new JobThreadExecutorBuilder()
                .setCorePoolSize(2)
                .setMaxPoolSize(10)
                .setKeepAliveTime(30L, TimeUnit.SECONDS)
                .setWorkQueue(new LinkedBlockingQueue<>(2000))
                .setThreadFactory(registryOrRemoveThreadFactory)
                .setHandler((Runnable runnable, ThreadPoolExecutor executor) -> { // 这里的拒绝策略是再次执行
                    runnable.run();
                    logger.warn(">>>>>>>>>>> job registry or remove too fast, match threadpool rejected handler(run now).");
                })
                .build();

        Runnable runnable = () -> {
            while (!toStop.get()) {
                try {
                    // 获取所有执行器是自动注册的数据
                    List<JobGroup> groupList = mapperFactory.getJobGroupMapper().findByAddressType(0);
                    if (CollectionUtils.isNotEmpty(groupList)) {
                        // 查询是否有心跳超时的注册执行器
                        List<Integer> ids = mapperFactory.getJobRegistryMapper().findDeadHandler(RegistryConfig.DEAD_TIMEOUT, new Date());
                        // 从注册列表中移除超时的注册执行器
                        if (CollectionUtils.isNotEmpty(ids)) {
                            mapperFactory.getJobRegistryMapper().removeDeadHandler(ids);
                        }

                        // 刷新在线的执行器
                        Map<String, List<String>> appAddressMap = new HashMap();
                        // 查询所有未超时的执行器集合
                        List<JobRegistry> activityHandlerList = mapperFactory.getJobRegistryMapper().findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
                        if (CollectionUtils.isNotEmpty(activityHandlerList)) {
                            for (JobRegistry jobRegistry: activityHandlerList) {
                                // 如果注册类型是 handler，则缓存 appName 和 注册列表
                                if (RegistryConfig.RegistType.EXECUTOR.name().equals(jobRegistry.getRegistryGroup())) {
                                    String appName = jobRegistry.getRegistryKey();
                                    List<String> registryList = appAddressMap.get(appName);
                                    if (CollectionUtils.isEmpty(registryList)) {
                                        registryList = new ArrayList<>(8);
                                    }

                                    if (!registryList.contains(jobRegistry.getRegistryValue())) {
                                        registryList.add(jobRegistry.getRegistryValue());
                                    }

                                    appAddressMap.put(appName, registryList);
                                }
                            }
                        }

                        // 刷新列表分组
                        for (JobGroup jobGroup: groupList) {
                            List<String> registryList = appAddressMap.get(jobGroup.getAppName());
                            String addressStr = null;
                            if (CollectionUtils.isNotEmpty(registryList)) {
                                Collections.sort(registryList);
                                StringJoiner joiner = new StringJoiner(",");
                                for (String item: registryList) {
                                    joiner.add(item);
                                }
                                addressStr = joiner.toString();
                            }
                            jobGroup.setAddressList(addressStr);
                            jobGroup.setUpdateTime(new Date());
                            mapperFactory.getJobGroupMapper().update(jobGroup);
                        }
                    }
                } catch (Exception ex) {
                    if (!toStop.get()) {
                        logger.error(">>>>>>>>>>> job registry monitor thread error:{}", ex);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                } catch (InterruptedException ex) {
                    if (!toStop.get()) {
                        logger.error(">>>>>>>>>>> job registry monitor thread error:{}", ex);
                    }
                }

                logger.info(">>>>>>>>>>> job registry monitor thread stop");
            }
        };
        registryMonitorThread = new Thread(runnable);
        registryMonitorThread.setDaemon(true);
        registryMonitorThread.setName("JobRegistryMonitorHelper-registryMonitorThread");
        registryMonitorThread.start();
    }


    public void toStop() {
        toStop.compareAndSet(false, true);

        // stop registryOrRemoveThreadPool
        registryOrRemoveThreadPool.shutdownNow();

        // stop monitir (interrupt and wait)
        registryMonitorThread.interrupt();
        try {
            registryMonitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    public Response registry(RegistryParam registryParam) {

        // valid
        if (StringUtils.isBlank(registryParam.getRegistryGroup())
                || StringUtils.isBlank(registryParam.getRegistryKey())
                || StringUtils.isBlank(registryParam.getRegistryValue())) {

            return Response.error("99999999", "Illegal Argument");
        }

        Runnable runnable = () -> {
            long id48 = SnowFlakeId.getInstance().generateId48();
            int ret = mapperFactory.getJobRegistryMapper().registryUpdate(
                    registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                    registryParam.getRegistryValue(), new Date());


            if (ret < 1) {
                mapperFactory.getJobRegistryMapper().registrySave(String.valueOf(id48),
                        registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                        registryParam.getRegistryValue(), new Date());

                // fresh
                freshGroupRegistryInfo(registryParam);
            }
        };

        // async execute
        registryOrRemoveThreadPool.execute(runnable);

        return Response.success("00000000");
    }

    public Response registryRemove(RegistryParam registryParam) {

        // valid
        if (StringUtils.isBlank(registryParam.getRegistryGroup())
                || StringUtils.isBlank(registryParam.getRegistryKey())
                || StringUtils.isBlank(registryParam.getRegistryValue())) {

            return Response.error("99999999", "Illegal Argument");
        }


        Runnable runnable = () -> {
            int ret = mapperFactory.getJobRegistryMapper().registryDelete(
                    registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                    registryParam.getRegistryValue());
            if (ret > 0) {
                // fresh
                freshGroupRegistryInfo(registryParam);
            }
        };

        // async execute
        registryOrRemoveThreadPool.execute(runnable);

        return Response.success("00000000");
    }

    private void freshGroupRegistryInfo(RegistryParam registryParam){
        // Under consideration, prevent affecting core tables
    }

}
