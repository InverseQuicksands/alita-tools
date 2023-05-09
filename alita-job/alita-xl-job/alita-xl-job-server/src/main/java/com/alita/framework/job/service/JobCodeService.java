package com.alita.framework.job.service;

import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLogGlue;

import java.util.List;

/**
 * JobCodeService
 *
 * @date 2022-12-26 15:25
 */
public interface JobCodeService {

    /**
     * 通过主键查询 JobInfo
     *
     * @param jobId jobId
     * @return JobInfo
     */
    JobInfo queryJobInfoById(String jobId);

    /**
     * 通过 jobId 查询 JobLogGlue
     *
     * @param jobId jobId
     * @return List
     */
    List<JobLogGlue> queryJobLogGlueById(String jobId);

    /**
     * 修改 JobInfo
     *
     * @param exists_jobInfo 信息
     */
    void updateJobInfo(JobInfo exists_jobInfo);

    /**
     * 保存 JobLogGlue
     *
     * @param jobLogGlue 信息
     */
    void saveJobLogGlue(JobLogGlue jobLogGlue);

    /**
     * 删除指定天数的数据
     *
     * @param id 主键
     * @param day 天数
     */
    void removeJobLogGlueOld(String id, int day);
}
