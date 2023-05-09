package com.alita.framework.job.model;

import java.io.Serializable;
import java.util.Date;

public class JobLogReport implements Serializable {

    private static final long serialVersionUID = -4527658738385997104L;

    /**
     * 主键
     */
    private String id;

    /**
     * 调度-时间
     */
    private Date triggerDay;

    /**
     * 运行中-日志数量
     */
    private int runningCount;

    /**
     * 执行成功-日志数量
     */
    private int sucCount;

    /**
     * 执行失败-日志数量
     */
    private int failCount;

    /**
     * 更新时间
     */
    private Date update_time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTriggerDay() {
        return triggerDay;
    }

    public void setTriggerDay(Date triggerDay) {
        this.triggerDay = triggerDay;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public int getSucCount() {
        return sucCount;
    }

    public void setSucCount(int sucCount) {
        this.sucCount = sucCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "JobLogReport{" +
                "id='" + id + '\'' +
                ", triggerDay='" + triggerDay + '\'' +
                ", runningCount=" + runningCount +
                ", sucCount=" + sucCount +
                ", failCount=" + failCount +
                ", update_time='" + update_time + '\'' +
                '}';
    }
}
