package com.alita.framework.job.core.thread;

import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.biz.model.TriggerParam;
import com.alita.framework.job.core.context.JobContext;
import com.alita.framework.job.core.context.JobHelper;
import com.alita.framework.job.core.executor.JobExecutor;
import com.alita.framework.job.core.executor.JobFileAppender;
import com.alita.framework.job.core.handler.IJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * JobThread
 *
 * @date 2022-11-24 23:05
 */
public class JobThread extends Thread{
    private static Logger logger = LoggerFactory.getLogger(JobThread.class);

    private String jobId;
    private IJobHandler handler;
    private LinkedBlockingQueue<TriggerParam> triggerQueue;

    // avoid repeat trigger for the same TRIGGER_LOG_ID
    private Set<Long> triggerLogIdSet;

    private volatile boolean toStop = false;
    private String stopReason;

    private boolean running = false;    // if running job
    private int idleTimes = 0;			// idel times


    public JobThread(String jobId, IJobHandler handler) {
        this.jobId = jobId;
        this.handler = handler;
        this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
        this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Long>());

        // assign job thread name
        this.setName("JobThread-"+jobId+"-"+System.currentTimeMillis());
    }
    public IJobHandler getHandler() {
        return handler;
    }

    /**
     * new trigger to queue
     *
     * @param triggerParam
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    public String pushTriggerQueue(TriggerParam triggerParam) {
        // avoid repeat
        if (triggerLogIdSet.contains(triggerParam.getLogId())) {
            logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
            return "99999999";
        }

        triggerLogIdSet.add(triggerParam.getLogId());
        triggerQueue.add(triggerParam);
        return "00000000";
    }

    /**
     * kill job thread
     *
     * @param stopReason
     */
    public void toStop(String stopReason) {
        /**
         * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
         * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
         * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
         */
        this.toStop = true;
        this.stopReason = stopReason;
    }

    /**
     * is running job
     * @return
     */
    public boolean isRunningOrHasQueue() {
        return running || triggerQueue.size()>0;
    }

    @Override
    public void run() {

        // init
        try {
            handler.init();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // execute
        while(!toStop){
            running = false;
            idleTimes++;

            TriggerParam triggerParam = null;
            try {
                // to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
                triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
                if (triggerParam!=null) {
                    running = true;
                    idleTimes = 0;
                    triggerLogIdSet.remove(triggerParam.getLogId());

                    // log filename, like "logPath/yyyy-MM-dd/9999.log"
                    String logFileName = JobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTime()), triggerParam.getLogId());
                    JobContext jobContext = new JobContext(
                            Long.valueOf(triggerParam.getJobId()),
                            triggerParam.getExecutorParams(),
                            logFileName,
                            triggerParam.getBroadcastIndex(),
                            triggerParam.getBroadcastTotal());

                    // init job context
                    JobContext.setJobContext(jobContext);

                    // execute
                    JobHelper.log("<br>----------- job execute start -----------<br>----------- Param:" + jobContext.getJobParam());

                    if (triggerParam.getExecutorTimeout() > 0) {
                        // limit timeout
                        Thread futureThread = null;
                        try {
                            FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {

                                    // init job context
                                    JobContext.setJobContext(jobContext);

                                    handler.execute();
                                    return true;
                                }
                            });
                            futureThread = new Thread(futureTask);
                            futureThread.start();

                            Boolean tempResult = futureTask.get(triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
                        } catch (TimeoutException e) {
                            JobHelper.log("<br>----------- job execute timeout");
                            JobHelper.log(e);

                            // handle result
                            JobHelper.handleTimeout("job execute timeout ");
                        } finally {
                            futureThread.interrupt();
                        }
                    } else {
                        // just execute
                        handler.execute();
                    }

                    // valid execute handle data
                    if (JobContext.getJobContext().getHandleCode().equals("99999999")) {
                        JobHelper.handleFail("job handle result lost.");
                    } else {
                        String tempHandleMsg = JobContext.getJobContext().getHandleMsg();
                        tempHandleMsg = (tempHandleMsg!=null&&tempHandleMsg.length()>50000)
                                ?tempHandleMsg.substring(0, 50000).concat("...")
                                :tempHandleMsg;
                        JobContext.getJobContext().setHandleMsg(tempHandleMsg);
                    }
                    JobHelper.log("<br>----------- job execute end(finish) -----------<br>----------- Result: handleCode="
                            + JobContext.getJobContext().getHandleCode()
                            + ", handleMsg = "
                            + JobContext.getJobContext().getHandleMsg()
                    );

                } else {
                    if (idleTimes > 30) {
                        if(triggerQueue.size() == 0) {	// avoid concurrent trigger causes jobId-lost
                            JobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
                        }
                    }
                }
            } catch (Throwable e) {
                if (toStop) {
                    JobHelper.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
                }

                // handle result
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                String errorMsg = stringWriter.toString();

                JobHelper.handleFail(errorMsg);

                JobHelper.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- brilliance-job job execute end(error) -----------");
            } finally {
                if(triggerParam != null) {
                    // callback handler info
                    if (!toStop) {
                        // commonm
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(
                                triggerParam.getLogId(),
                                triggerParam.getLogDateTime(),
                                JobContext.getJobContext().getHandleCode(),
                                JobContext.getJobContext().getHandleMsg() )
                        );
                    } else {
                        // is killed
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(
                                triggerParam.getLogId(),
                                triggerParam.getLogDateTime(),
                                JobContext.HANDLE_CODE_FAIL,
                                stopReason + " [job running, killed]" )
                        );
                    }
                }
            }
        }

        // callback trigger request in queue
        while(triggerQueue !=null && triggerQueue.size()>0){
            TriggerParam triggerParam = triggerQueue.poll();
            if (triggerParam!=null) {
                // is killed
                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(
                        triggerParam.getLogId(),
                        triggerParam.getLogDateTime(),
                        JobContext.HANDLE_CODE_FAIL,
                        stopReason + " [job not executed, in the job queue, killed.]")
                );
            }
        }

        // destroy
        try {
            handler.destroy();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        logger.info(">>>>>>>>>>> job JobThread stoped, hashCode:{}", Thread.currentThread());
    }
}
