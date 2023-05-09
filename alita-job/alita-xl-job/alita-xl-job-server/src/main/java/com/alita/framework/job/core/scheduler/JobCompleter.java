package com.alita.framework.job.core.scheduler;

import com.alita.framework.job.config.JobServerConfig;
import com.alita.framework.job.core.context.JobContext;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.core.thread.JobTriggerPoolHelper;
import com.alita.framework.job.core.trigger.TriggerTypeEnum;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLog;
import com.alita.framework.job.utils.I18nUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * 任务失败处理.
 *
 * <p>如果有子任务，则继续执行子任务.
 */
public class JobCompleter {

    private static Logger logger = LoggerFactory.getLogger(JobCompleter.class);

    private static final MapperFactory mapperFactory = JobServerConfig.getMapperFactory();

    /**
     * common fresh handle entrance (limit only once)
     *
     * @param jobLog
     * @return
     */
    public static int updateHandleInfoAndFinish(JobLog jobLog) {
        // finish 若父任务正常结束，则继续执行子任务,以及设置Childmsg
        finishJob(jobLog);

        // text最大64kb 避免长度过长，截断超过长度限制字符
        if (jobLog.getHandleMsg().length() > 15000) {
            jobLog.setHandleMsg(jobLog.getHandleMsg().substring(0, 15000));
        }

        // fresh handle
        return mapperFactory.getJobLogMapper().updateHandleInfo(jobLog);
    }


    /**
     * do somethind to finish job
     */
    private static void finishJob(JobLog jobLog) {
        // 1、handle success, to trigger child job
        String triggerChildMsg = null;
        // 如果执行状态是成功
        if (JobContext.HANDLE_CODE_SUCCESS == jobLog.getHandleCode()) {
            JobInfo jobInfo = mapperFactory.getJobInfoMapper().queryById(jobLog.getJobId());
            // 如果 jobInfo 不为空且有对应的子任务
            if (jobInfo != null && StringUtils.isNotBlank(jobInfo.getChildJobId())) {
                triggerChildMsg = I18nUtils.getProperty("jobconf_trigger_child_run");

                String[] childJobIds = jobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    String childJobId = null;
                    if (StringUtils.isNotBlank(childJobIds[i]) && StringUtils.isNumeric(childJobIds[i])) {
                        childJobId = childJobIds[i];
                    } else {
                        childJobId = "-1";
                    }

                    if (Integer.valueOf(childJobId) > 0) {
                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
                        // add msg
                        triggerChildMsg += MessageFormat.format(
                                I18nUtils.getProperty("jobconf_callback_child_msg1"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i],
                                I18nUtils.getProperty("system_success")
                        );
                    } else {
                        triggerChildMsg += MessageFormat.format(
                                I18nUtils.getProperty("jobconf_callback_child_msg2"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i]
                        );
                    }
                }

            }
        }
        if (triggerChildMsg != null) {
            jobLog.setHandleMsg(jobLog.getHandleMsg() + triggerChildMsg);
        }
    }

}
