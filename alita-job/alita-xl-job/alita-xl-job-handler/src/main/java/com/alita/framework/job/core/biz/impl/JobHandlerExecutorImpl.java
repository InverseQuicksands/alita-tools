package com.alita.framework.job.core.biz.impl;

import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.biz.model.LogParam;
import com.alita.framework.job.core.biz.model.LogResult;
import com.alita.framework.job.core.biz.model.TriggerParam;
import com.alita.framework.job.core.enums.ExecutorBlockStrategyEnum;
import com.alita.framework.job.core.executor.JobExecutor;
import com.alita.framework.job.core.executor.JobFileAppender;
import com.alita.framework.job.core.glue.GlueFactory;
import com.alita.framework.job.core.glue.GlueTypeEnum;
import com.alita.framework.job.core.handler.IJobHandler;
import com.alita.framework.job.core.handler.impl.GlueJobHandler;
import com.alita.framework.job.core.handler.impl.ScriptJobHandler;
import com.alita.framework.job.core.thread.JobThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * JobHandlerExecutorImpl
 *
 * @date 2022-11-25 22:45
 */
public class JobHandlerExecutorImpl implements JobHandlerExecutor {

    private static Logger logger = LoggerFactory.getLogger(JobHandlerExecutorImpl.class);

    /**
     * 是否空闲
     *
     * @param executorParam 执行参数
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    @Override
    public String idleBeat(ExecutorParam executorParam) throws IOException {
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = JobExecutor.loadJobThread(executorParam.getJobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            logger.warn("job thread is running or has trigger queue");
            return "99999999";
        }
        return "00000000";
    }

    /**
     * 心跳检测
     *
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    @Override
    public String beat() throws IOException {
        return "00000000";
    }

    /**
     * 运行任务
     *
     * @param triggerParam 执行参数
     */
    @Override
    public String run(TriggerParam triggerParam) throws IOException {
        // load old：jobHandler + jobThread
        JobThread jobThread = JobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;

        // valid：jobHandler + jobThread
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        if (GlueTypeEnum.BEAN == glueTypeEnum) {

            // new jobhandler
            IJobHandler newJobHandler = JobExecutor.loadJobHandler(triggerParam.getExecutorHandler());

            // valid old jobThread
            if (jobThread != null && jobHandler != newJobHandler) {
                // change handler, need kill old thread
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    logger.warn("job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                    return "99999999";
                }
            }

        } else if (GlueTypeEnum.GLUE_GROOVY == glueTypeEnum) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof GlueJobHandler
                            && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdatetime() == triggerParam.getGlueUpdatetime())) {
                // change handler or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                try {
                    IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                    jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatetime());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return "99999999";
                }
            }
        } else if (glueTypeEnum != null && glueTypeEnum.isScript()) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof ScriptJobHandler
                            && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdatetime() == triggerParam.getGlueUpdatetime())) {
                // change script or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        } else {
            logger.warn("glueType[" + triggerParam.getGlueType() + "] is not valid.");
            return "99999999";
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    logger.warn("block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                    return "99999999";
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = JobExecutor.registJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue
        String pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }

    /**
     * 关闭任务
     *
     * @param killParam 执行参数
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    @Override
    public String kill(ExecutorParam killParam) throws IOException {
        // kill handlerThread, and create new one
        JobThread jobThread = JobExecutor.loadJobThread(killParam.getJobId());
        if (jobThread != null) {
            JobExecutor.removeJobThread(killParam.getJobId(), "scheduling center kill job.");
            return "00000000";
        }

        logger.warn("job thread already killed.");
        return "00000000";
    }

    /**
     * log
     *
     * @param logParam 执行参数
     * @return LogResult
     */
    @Override
    public LogResult log(LogParam logParam) throws IOException {
        // log filename: logPath/yyyy-MM-dd/9999.log
        String logFileName = JobFileAppender.makeLogFileName(new Date(logParam.getLogDateTim()), logParam.getLogId());

        LogResult logResult = JobFileAppender.readLog(logFileName, logParam.getFromLineNum());
        return logResult;
    }
}
