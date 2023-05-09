package com.alita.framework.job.core.thread;

import com.alita.framework.job.core.biz.JobAdminExecutor;
import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.context.JobContext;
import com.alita.framework.job.core.context.JobHelper;
import com.alita.framework.job.core.enums.RegistryConfig;
import com.alita.framework.job.core.executor.JobExecutor;
import com.alita.framework.job.core.executor.JobFileAppender;
import com.alita.framework.job.utils.CollectionUtils;
import com.alita.framework.job.utils.JdkSerializeTool;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TriggerCallbackThread
 *
 * @date 2022-11-24 22:33
 */
public class TriggerCallbackThread {

    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static class TriggerCallbackThreadSington {
        private static final TriggerCallbackThread triggerCallbackThread = new TriggerCallbackThread();
    }

    private TriggerCallbackThread() {

    }

    public static TriggerCallbackThread getInstance() {
        return TriggerCallbackThreadSington.triggerCallbackThread;
    }

    /**
     * callback thread
     */
    private Thread triggerCallbackThread;
    private Thread triggerRetryCallbackThread;
    private AtomicBoolean toStop = new AtomicBoolean(false);

    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<HandleCallbackParam>();

    public static void pushCallBack(HandleCallbackParam callback){
        getInstance().callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> job push callback request, logId:{}", callback.getLogId());
    }

    public void start() {
        // valid
        if (JobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> job, executor callback config fail, adminAddresses is null.");
            return;
        }


        Runnable triggerCallbackRunnable = () -> {
            // normal callback
            while(!toStop.get()){
                try {
                    HandleCallbackParam callback = getInstance().callBackQueue.take();
                    if (callback != null) {
                        // callback list param
                        List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
                        // 将 callBackQueue 队列中元素转移到 callbackParamList
                        int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
                        callbackParamList.add(callback);

                        // callback, will retry if error
                        if (CollectionUtils.isNotEmpty(callbackParamList)) {
                            doCallback(callbackParamList);
                        }
                    }
                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            // last callback
            try {
                List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
                int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
                if (CollectionUtils.isNotEmpty(callbackParamList)) {
                    doCallback(callbackParamList);
                }
            } catch (Exception e) {
                if (!toStop.get()) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.info(">>>>>>>>>>> job executor callback thread destroy.");
        };

        triggerCallbackThread = new Thread(triggerCallbackRunnable);
        triggerCallbackThread.setDaemon(true);
        triggerCallbackThread.setName("TriggerCallbackThread");
        triggerCallbackThread.start();


        Runnable triggerRetryCallbackRunnable = () -> {
            while(!toStop.get()){
                try {
                    retryFailCallbackFile();
                } catch (Exception e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                } catch (InterruptedException e) {
                    if (!toStop.get()) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.info(">>>>>>>>>>> job executor retry callback thread destroy.");
        };

        triggerRetryCallbackThread = new Thread(triggerRetryCallbackRunnable);
        triggerRetryCallbackThread.setDaemon(true);
        triggerRetryCallbackThread.start();
    }


    public void toStop(){
        toStop.compareAndSet(false, true);
        // stop callback, interrupt and wait
        if (triggerCallbackThread != null) {    // support empty admin address
            triggerCallbackThread.interrupt();
            try {
                triggerCallbackThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        // stop retry, interrupt and wait
        if (triggerRetryCallbackThread != null) {
            triggerRetryCallbackThread.interrupt();
            try {
                triggerRetryCallbackThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * do callback, will retry if error
     * @param callbackParamList
     */
    private void doCallback(List<HandleCallbackParam> callbackParamList){
        boolean callbackRet = false;
        // callback, will retry if error
        for (JobAdminExecutor adminBiz: JobExecutor.getAdminBizList()) {
            try {
                // TODO 返回响应码：成功-00000000，失败-99999999
                String callbackResult = adminBiz.callback(callbackParamList);
                if (callbackResult !=null && "00000000".equals(callbackResult)) {
                    callbackLog(callbackParamList, "<br>----------- job callback finish.");
                    callbackRet = true;
                    break;
                } else {
                    callbackLog(callbackParamList, "<br>----------- job callback fail, callbackResult:" + callbackResult);
                }
            } catch (Exception e) {
                callbackLog(callbackParamList, "<br>----------- job callback error, errorMsg:" + e.getMessage());
            }
        }
        if (!callbackRet) {
            appendFailCallbackFile(callbackParamList);
        }
    }


    /**
     * callback log
     */
    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent){
        for (HandleCallbackParam callbackParam: callbackParamList) {
            String logFileName = JobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTim()), callbackParam.getLogId());
            JobContext.setJobContext(new JobContext(-1, null, logFileName, -1, -1));
            JobHelper.log(logContent);
        }
    }


    // ---------------------- fail-callback file ----------------------

    private static String failCallbackFilePath = JobFileAppender.getLogPath().concat(File.separator).concat("callbacklog").concat(File.separator);
    private static String failCallbackFileName = failCallbackFilePath.concat("job-callback-{x}").concat(".log");

    private void appendFailCallbackFile(List<HandleCallbackParam> callbackParamList){
        // valid
        if (CollectionUtils.isEmpty(callbackParamList)) {
            return;
        }

        // append file
        byte[] callbackParamList_bytes = JdkSerializeTool.serialize(callbackParamList);

        File callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis())));
        if (callbackLogFile.exists()) {
            for (int i = 0; i < 100; i++) {
                callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis()).concat("-").concat(String.valueOf(i)) ));
                if (!callbackLogFile.exists()) {
                    break;
                }
            }
        }
        try {
            FileUtils.writeByteArrayToFile(callbackLogFile, callbackParamList_bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void retryFailCallbackFile(){
        // valid
        File callbackLogPath = new File(failCallbackFilePath);
        if (!callbackLogPath.exists()) {
            return;
        }
        if (callbackLogPath.isFile()) {
            callbackLogPath.delete();
        }
        if (!(callbackLogPath.isDirectory() && callbackLogPath.list() !=null && callbackLogPath.list().length>0)) {
            return;
        }

        // load and clear file, retry
        for (File callbaclLogFile: callbackLogPath.listFiles()) {
            byte[] callbackParamList_bytes = null;
            try {
                callbackParamList_bytes = FileUtils.readFileToByteArray(callbaclLogFile);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            // avoid empty file
            if(callbackParamList_bytes == null || callbackParamList_bytes.length < 1){
                callbaclLogFile.delete();
                continue;
            }

            List<HandleCallbackParam> callbackParamList = (List<HandleCallbackParam>) JdkSerializeTool.deserialize(callbackParamList_bytes, List.class);
            callbaclLogFile.delete();
            doCallback(callbackParamList);
        }
    }

}
