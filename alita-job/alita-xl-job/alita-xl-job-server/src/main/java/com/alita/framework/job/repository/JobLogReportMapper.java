package com.alita.framework.job.repository;

import com.alita.framework.job.model.JobLogReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobLogReportMapper {


    /**
     * 修改 JobLogReport
     *
     * @param jobLogReport 信息
     */
    int update(JobLogReport jobLogReport);

    /**
     * 新增 JobLogReport
     *
     * @param jobLogReport 信息
     */
    int save(JobLogReport jobLogReport);
}
