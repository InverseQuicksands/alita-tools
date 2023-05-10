package com.alita.framework.job.core.factory;

import com.alita.framework.job.repository.*;

/**
 * mapper 工厂
 */
public class DefaultMapperFactory implements MapperFactory {

    private JobGroupMapper jobGroupMapper;
    private JobInfoMapper jobInfoMapper;
    private JobLogGlueMapper jobLogGlueMapper;
    private JobLogMapper jobLogMapper;
    private JobLogReportMapper jobLogReportMapper;
    private JobRegistryMapper jobRegistryMapper;


    public JobGroupMapper getJobGroupMapper() {
        return jobGroupMapper;
    }

    public void setJobGroupMapper(JobGroupMapper jobGroupMapper) {
        this.jobGroupMapper = jobGroupMapper;
    }

    public JobInfoMapper getJobInfoMapper() {
        return jobInfoMapper;
    }

    public void setJobInfoMapper(JobInfoMapper jobInfoMapper) {
        this.jobInfoMapper = jobInfoMapper;
    }

    public JobLogGlueMapper getJobLogGlueMapper() {
        return jobLogGlueMapper;
    }

    public void setJobLogGlueMapper(JobLogGlueMapper jobLogGlueMapper) {
        this.jobLogGlueMapper = jobLogGlueMapper;
    }

    public JobLogMapper getJobLogMapper() {
        return jobLogMapper;
    }

    public void setJobLogMapper(JobLogMapper jobLogMapper) {
        this.jobLogMapper = jobLogMapper;
    }

    public JobLogReportMapper getJobLogReportMapper() {
        return jobLogReportMapper;
    }

    public void setJobLogReportMapper(JobLogReportMapper jobLogReportMapper) {
        this.jobLogReportMapper = jobLogReportMapper;
    }

    public JobRegistryMapper getJobRegistryMapper() {
        return jobRegistryMapper;
    }

    public void setJobRegistryMapper(JobRegistryMapper jobRegistryMapper) {
        this.jobRegistryMapper = jobRegistryMapper;
    }

}
