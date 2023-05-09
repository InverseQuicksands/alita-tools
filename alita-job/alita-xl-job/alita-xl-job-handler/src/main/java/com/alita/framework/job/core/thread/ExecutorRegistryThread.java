package com.alita.framework.job.core.thread;

import com.alita.framework.job.core.biz.JobAdminExecutor;
import com.alita.framework.job.core.biz.model.RegistryParam;
import com.alita.framework.job.core.enums.RegistryConfig;
import com.alita.framework.job.core.executor.JobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ExecutorRegistryThread
 *
 * @date 2022-11-25 23:15
 */
public class ExecutorRegistryThread {

    public static final Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);


    private static class ExecutorRegistryThreadSington {
        private static final ExecutorRegistryThread instance = new ExecutorRegistryThread();
    }

    public static ExecutorRegistryThread getInstance() {
        return ExecutorRegistryThreadSington.instance;
    }

    private ExecutorRegistryThread() {

    }

    private Thread registryThread;
    private AtomicBoolean toStop = new AtomicBoolean(false);


    public void start(final String appname, final String address){

        // valid
        if (appname==null || appname.trim().length()==0) {
            logger.warn(">>>>>>>>>>> brilliance-job, executor registry config fail, appname is null.");
            return;
        }
        if (JobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> brilliance-job, executor registry config fail, adminAddresses is null.");
            return;
        }

        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // registry
                while (!toStop.get()) {
                    try {
                        RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appname, address);
                        for (JobAdminExecutor adminExecutor: JobExecutor.getAdminBizList()) {
                            try {
                                String registryResult = adminExecutor.registry(registryParam);
                                if (registryResult !=null && "00000000".equals(registryResult)) {
                                    logger.debug(">>>>>>>>>>> brilliance-job registry success, registryUrl:{}, registryParam:{}, registryResult:{}", adminExecutor.getAddressUrl(), registryParam, registryResult);
                                    break;
                                } else {
                                    logger.info(">>>>>>>>>>> brilliance-job registry fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                }
                            } catch (Exception e) {
                                logger.info(">>>>>>>>>>> brilliance-job registry error, registryParam:{}", registryParam, e);
                            }
                        }
                    } catch (Exception e) {
                        if (!toStop.get()) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    try {
                        if (!toStop.get()) {
                            TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                        }
                    } catch (InterruptedException e) {
                        if (!toStop.get()) {
                            logger.warn(">>>>>>>>>>> brilliance-job, executor registry thread interrupted, error msg:{}", e.getMessage());
                        }
                    }
                }

                // registry remove
                try {
                    RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appname, address);
                    for (JobAdminExecutor adminExecutor: JobExecutor.getAdminBizList()) {
                        try {
                            String registryResult = adminExecutor.registryRemove(registryParam);
                            if (registryResult !=null && "00000000".equals(registryResult)) {
                                logger.info(">>>>>>>>>>> brilliance-job registry-remove success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                break;
                            } else {
                                logger.info(">>>>>>>>>>> brilliance-job registry-remove fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            }
                        } catch (Exception e) {
                            if (!toStop.get()) {
                                logger.info(">>>>>>>>>>> brilliance-job registry-remove error, registryParam:{}", registryParam, e);
                            }

                        }

                    }
                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info(">>>>>>>>>>> brilliance-job, executor registry thread destroy.");

            }
        });
        registryThread.setDaemon(true);
        registryThread.setName("brilliance-job, executor ExecutorRegistryThread");
        registryThread.start();
    }

    public void toStop() {
        toStop.compareAndSet(false, true);

        // interrupt and wait
        if (registryThread != null) {
            registryThread.interrupt();
            try {
                registryThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

    }




}
