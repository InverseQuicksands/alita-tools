package com.alita.framework.job.dto;

import com.alita.framework.job.common.AbstractQueryPage;

/**
 * 任务管理 dto
 */
public class JobInfoDto extends AbstractQueryPage {

    /**
     * 执行器实例名称
     */
    private String jobGroup;

    /**
     * 执行状态：-1：全部；0-停止；1：启动
     */
    private String triggerStatus;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * 任务名称（指具体执行的方法）
     */
    private String executorHandler;

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(String triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    @Override
    public String toString() {
        return "JobInfoDto{" +
                "jobGroup='" + jobGroup + '\'' +
                ", triggerStatus='" + triggerStatus + '\'' +
                ", jobDesc='" + jobDesc + '\'' +
                ", executorHandler='" + executorHandler + '\'' +
                "} " + super.toString();
    }
}
