package com.alita.framework.job.service;

import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.dto.JobLogDto;
import com.alita.framework.job.model.JobLog;

import java.util.Date;
import java.util.List;

/**
 * JobLogService
 *
 * @date 2022-12-26 17:07
 */
public interface JobLogService {


    PageInfo<JobLog> pageList(JobLogDto jobLogDto);

    JobLog queryById(String id);

    List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum);

    void clearLog(List<Long> logIds);
}
