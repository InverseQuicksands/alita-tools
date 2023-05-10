package com.alita.framework.job.core.alarmer;

import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLog;

public interface JobAlarm {

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean doAlarm(JobInfo info, JobLog jobLog);

}
