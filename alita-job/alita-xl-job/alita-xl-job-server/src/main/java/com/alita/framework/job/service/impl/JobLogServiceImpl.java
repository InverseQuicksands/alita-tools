package com.alita.framework.job.service.impl;

import com.alita.framework.job.common.AbstractServiceImpl;
import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.dto.JobLogDto;
import com.alita.framework.job.model.JobLog;
import com.alita.framework.job.repository.JobGroupMapper;
import com.alita.framework.job.repository.JobInfoMapper;
import com.alita.framework.job.repository.JobLogMapper;
import com.alita.framework.job.service.JobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * JobLogServiceImpl
 *
 * @date 2022-12-26 17:07
 */
@Service
public class JobLogServiceImpl extends AbstractServiceImpl<JobLog> implements JobLogService {

    @Autowired
    private JobGroupMapper jobGroupMapper;

    @Autowired
    public JobInfoMapper jobInfoMapper;

    @Autowired
    public JobLogMapper jobLogMapper;


    /**
     * @param jobLogDto
     * @return
     */
    @Override
    public PageInfo<JobLog> pageList(JobLogDto jobLogDto) {
        List<JobLog> list = jobLogMapper.pageList(jobLogDto.getCurrentPage(), jobLogDto.getPageSize(), jobLogDto.getJobGroupId(),
                jobLogDto.getJobId(), jobLogDto.getTriggerTimeStart(), jobLogDto.getTriggerTimeEnd(),
                jobLogDto.getLogStatus());

        int count = jobLogMapper.pageListCount(jobLogDto.getJobGroupId(),
                jobLogDto.getJobId(), jobLogDto.getTriggerTimeStart(), jobLogDto.getTriggerTimeEnd(),
                jobLogDto.getLogStatus());

        return pageInfo(jobLogDto, list, count);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public JobLog queryById(String id) {
        return jobLogMapper.queryById(id);
    }

    /**
     * @param jobGroup
     * @param jobId
     * @param clearBeforeTime
     * @param clearBeforeNum
     * @return
     */
    @Override
    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum) {
        List<Long> logIds = jobLogMapper.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);

        return logIds;
    }

    /**
     * @param logIds
     */
    @Override
    public void clearLog(List<Long> logIds) {
        jobLogMapper.clearLog(logIds);
    }

}
