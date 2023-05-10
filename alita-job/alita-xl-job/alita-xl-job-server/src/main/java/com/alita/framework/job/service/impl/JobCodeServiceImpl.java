package com.alita.framework.job.service.impl;

import com.alita.framework.job.common.AbstractServiceImpl;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLogGlue;
import com.alita.framework.job.repository.JobInfoMapper;
import com.alita.framework.job.repository.JobLogGlueMapper;
import com.alita.framework.job.service.JobCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JobCodeServiceImpl
 *
 * @date 2022-12-26 15:26
 */
@Service
public class JobCodeServiceImpl extends AbstractServiceImpl implements JobCodeService {

    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Autowired
    private JobLogGlueMapper jobLogGlueMapper;


    /**
     * 通过主键查询 JobInfo
     *
     * @return JobInfo
     */
    @Override
    public JobInfo queryJobInfoById(String jobId) {
        return jobInfoMapper.queryById(jobId);
    }

    /**
     * 通过 jobId 查询 JobLogGlue
     *
     * @param jobId jobId
     * @return List
     */
    @Override
    public List<JobLogGlue> queryJobLogGlueById(String jobId) {
        return jobLogGlueMapper.queryByJobId(jobId);
    }

    /**
     * 修改 JobInfo
     *
     * @param exists_jobInfo 信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void updateJobInfo(JobInfo exists_jobInfo) {
        jobInfoMapper.update(exists_jobInfo);
    }

    /**
     * 保存 JobLogGlue
     *
     * @param jobLogGlue 信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void saveJobLogGlue(JobLogGlue jobLogGlue) {
        jobLogGlueMapper.save(jobLogGlue);
    }

    /**
     * 删除指定天数的数据
     *
     * @param id
     * @param day
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void removeJobLogGlueOld(String id, int day) {
        jobLogGlueMapper.removeOld(id, day);
    }
}
