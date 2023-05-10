package com.alita.framework.job.dto;

import com.alita.framework.job.common.AbstractQueryPage;

import java.io.Serializable;
import java.util.Date;

/**
 * JobLogDto
 *
 * @date 2022-12-27 13:42
 */
public class JobLogDto extends AbstractQueryPage implements Serializable {

    private static final long serialVersionUID = 3819530397513015533L;

    /**
     * 执行器 id
     */
    private String jobGroupId;

    /**
     * 任务 id
     */
    private String jobId;

    /**
     * 状态：全部：-1，成功：1，失败：2，进行中：3
     */
    private int logStatus;

    /**
     * 调度时间
     */
    private String filterTime;

    private Date triggerTimeStart;

    private Date triggerTimeEnd;

    public String getJobGroupId() {
        return jobGroupId;
    }

    public void setJobGroupId(String jobGroupId) {
        this.jobGroupId = jobGroupId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getLogStatus() {
        return logStatus;
    }

    public void setLogStatus(int logStatus) {
        this.logStatus = logStatus;
    }

    public String getFilterTime() {
        return filterTime;
    }

    public void setFilterTime(String filterTime) {
        this.filterTime = filterTime;
    }

    public Date getTriggerTimeStart() {
        return triggerTimeStart;
    }

    public void setTriggerTimeStart(Date triggerTimeStart) {
        this.triggerTimeStart = triggerTimeStart;
    }

    public Date getTriggerTimeEnd() {
        return triggerTimeEnd;
    }

    public void setTriggerTimeEnd(Date triggerTimeEnd) {
        this.triggerTimeEnd = triggerTimeEnd;
    }

    @Override
    public String toString() {
        return "JobLogDto{" +
                "jobGroupId='" + jobGroupId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", logStatus=" + logStatus +
                ", filterTime='" + filterTime + '\'' +
                ", triggerTimeStart=" + triggerTimeStart +
                ", triggerTimeEnd=" + triggerTimeEnd +
                "} " + super.toString();
    }
}
