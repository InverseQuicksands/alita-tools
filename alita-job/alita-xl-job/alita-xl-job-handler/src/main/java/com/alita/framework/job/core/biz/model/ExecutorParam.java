package com.alita.framework.job.core.biz.model;

import java.io.Serializable;

public class ExecutorParam implements Serializable {

    private static final long serialVersionUID = -15029819090987594L;

    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        return "ExecutorParam{" +
                "jobId='" + jobId + '\'' +
                '}';
    }
}
