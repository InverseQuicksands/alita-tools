package com.alita.framework.job.core.thread;

import com.alita.framework.job.core.executor.JobFileAppender;
import com.alita.framework.job.utils.DateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JobLogFileCleanThread
 *
 * @date 2022-11-21 23:28
 */
public class JobLogFileCleanThread {

    private static Logger logger = LoggerFactory.getLogger(JobLogFileCleanThread.class);

    private static class JobLogFileCleanThreadSington {
        private static final JobLogFileCleanThread jobLogFileCleanThread = new JobLogFileCleanThread();
    }

    private JobLogFileCleanThread() {

    }

    public static JobLogFileCleanThread getInstance() {
        return JobLogFileCleanThreadSington.jobLogFileCleanThread;
    }

    private Thread localThread;

    private AtomicBoolean toStop = new AtomicBoolean(false);



    public void start(final long logRetentionDays) {
        // limit min value
        if (logRetentionDays < 3 ) {
            return;
        }

        Runnable runnable = () -> {
            while (!toStop.get()) {
                try {
                    // clean log dir, over logRetentionDays
                    File[] childDirs = new File(JobFileAppender.getLogPath()).listFiles();
                    if (childDirs!=null && childDirs.length>0) {
                        // today
                        LocalTime localTime = LocalTime.of(0, 0, 0);
                        long epochMilli = LocalDateTime.of(LocalDate.now(), localTime)
                                .toInstant(DateUtils.offset)
                                .toEpochMilli();

                        for (File childFile: childDirs) {
                            // valid
                            if (!childFile.isDirectory()) {
                                continue;
                            }
                            if (childFile.getName().indexOf("-") == -1) {
                                continue;
                            }

                            // file create date
                            long fileEpochMilli = DateUtils.toEpochMilli(childFile.getName());

                            if ((epochMilli-fileEpochMilli) >= logRetentionDays * (24 * 60 * 60 * 1000) ) {
                                FileUtils.deleteQuietly(childFile);
                            }
                        }
                    }

                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }

                try {
                    TimeUnit.DAYS.sleep(1);
                } catch (InterruptedException e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.info(">>>>>>>>>>> job executor JobLogFileCleanThread thread destroy.");
        };

        localThread = new Thread(runnable);
        localThread.setDaemon(true);
        localThread.setName("JobLogFileCleanThread");
        localThread.start();
    }



    public void toStop() {
        toStop.set(true);
        if (localThread == null) {
            return;
        }
        // interrupt and wait
        localThread.interrupt();
        try {
            localThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
