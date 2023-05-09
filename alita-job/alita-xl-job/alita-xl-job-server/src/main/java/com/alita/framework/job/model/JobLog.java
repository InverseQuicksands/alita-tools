package com.alita.framework.job.model;

import java.io.Serializable;
import java.util.Date;

public class JobLog implements Serializable {

    private static final long serialVersionUID = 3795533758954965702L;

    /**
     * 主键
     */
    private String id;

    /**
     * 执行器主键ID
     */
    private String jobGroup;

    /**
     * 任务ID
     */
    private String jobId;

    /**
     * 执行器地址
     */
    private String executorAddress;

    /**
     * 执行器任务handler
     */
    private String executorHandler;

    /**
     * 执行器任务参数
     */
    private String executorParam;

    /**
     * 执行器任务分片参数，格式如 1/2
     */
    private String executorShardingParam;

    /**
     * 失败重试次数
     */
    private long executorFailRetryCount;

    /**
     * 调度-时间
     */
    private Date triggerTime;

    /**
     * 调度-结果: 1-成功，0-失败
     */
    private long triggerCode;

    /**
     * 调度-日志
     */
    private String triggerMsg;

    /**
     * 执行-时间
     */
    private Date handleTime;

    /**
     * 执行-状态: 00000000-成功，99999999-失败
     */
    private String handleCode;

    /**
     * 执行-日志
     */
    private String handleMsg;

    /**
     * 告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败
     */
    private int alarmStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getExecutorAddress() {
        return executorAddress;
    }

    public void setExecutorAddress(String executorAddress) {
        this.executorAddress = executorAddress;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public String getExecutorShardingParam() {
        return executorShardingParam;
    }

    public void setExecutorShardingParam(String executorShardingParam) {
        this.executorShardingParam = executorShardingParam;
    }

    public long getExecutorFailRetryCount() {
        return executorFailRetryCount;
    }

    public void setExecutorFailRetryCount(long executorFailRetryCount) {
        this.executorFailRetryCount = executorFailRetryCount;
    }

    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }

    public long getTriggerCode() {
        return triggerCode;
    }

    public void setTriggerCode(long triggerCode) {
        this.triggerCode = triggerCode;
    }

    public String getTriggerMsg() {
        return triggerMsg;
    }

    public void setTriggerMsg(String triggerMsg) {
        this.triggerMsg = triggerMsg;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public String getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(String handleCode) {
        this.handleCode = handleCode;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }


    @Override
    public String toString() {
        return "JobLog{" +
                "id='" + id + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", jobId='" + jobId + '\'' +
                ", executorAddress='" + executorAddress + '\'' +
                ", executorHandler='" + executorHandler + '\'' +
                ", executorParam='" + executorParam + '\'' +
                ", executorShardingParam='" + executorShardingParam + '\'' +
                ", executorFailRetryCount=" + executorFailRetryCount +
                ", triggerTime=" + triggerTime +
                ", triggerCode=" + triggerCode +
                ", triggerMsg='" + triggerMsg + '\'' +
                ", handleTime=" + handleTime +
                ", handleCode='" + handleCode + '\'' +
                ", handleMsg='" + handleMsg + '\'' +
                ", alarmStatus=" + alarmStatus +
                '}';
    }
}
