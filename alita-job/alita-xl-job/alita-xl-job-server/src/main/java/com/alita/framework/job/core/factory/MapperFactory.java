package com.alita.framework.job.core.factory;

import com.alita.framework.job.repository.*;

public interface MapperFactory {

    JobGroupMapper getJobGroupMapper();

    JobInfoMapper getJobInfoMapper();

    JobLogGlueMapper getJobLogGlueMapper();

    JobLogMapper getJobLogMapper();

    JobLogReportMapper getJobLogReportMapper();

    JobRegistryMapper getJobRegistryMapper();

}
