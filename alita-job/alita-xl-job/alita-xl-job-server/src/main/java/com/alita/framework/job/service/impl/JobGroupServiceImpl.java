package com.alita.framework.job.service.impl;

import com.alita.framework.job.common.AbstractServiceImpl;
import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.core.enums.RegistryConfig;
import com.alita.framework.job.dto.JobGroupDto;
import com.alita.framework.job.model.JobGroup;
import com.alita.framework.job.model.JobRegistry;
import com.alita.framework.job.repository.JobGroupMapper;
import com.alita.framework.job.repository.JobInfoMapper;
import com.alita.framework.job.repository.JobRegistryMapper;
import com.alita.framework.job.service.JobGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * JobGroupServiceImpl
 *
 * @date 2022-12-26 11:05
 */
@Service
public class JobGroupServiceImpl extends AbstractServiceImpl<JobGroup> implements JobGroupService {

    @Autowired
    public JobInfoMapper jobInfoMapper;
    @Autowired
    public JobGroupMapper jobGroupMapper;
    @Autowired
    private JobRegistryMapper jobRegistryMapper;


    /**
     * 分页查询
     *
     * @param jobGroupDto
     * @return 数据
     */
    @Override
    public PageInfo<JobGroup> pageList(JobGroupDto jobGroupDto) {
        List<JobGroup> jobGroupList = jobGroupMapper.pageList(jobGroupDto.getCurrentPage(), jobGroupDto.getPageSize(),
                jobGroupDto.getAppName(), jobGroupDto.getTitle());

        int count = jobGroupMapper.pageListCount(jobGroupDto.getAppName(), jobGroupDto.getTitle());

        return pageInfo(jobGroupDto, jobGroupList, count);
    }

    /**
     * 新增 JobGroup
     *
     * @param jobGroup 信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void save(JobGroup jobGroup) {
        jobGroup.setId(String.valueOf(increment()));
        jobGroupMapper.save(jobGroup);
    }

    /**
     * 修改 JobGroup
     *
     * @param jobGroup 信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void update(JobGroup jobGroup) {
        jobGroupMapper.update(jobGroup);
    }

    /**
     * 删除 JobGroup
     *
     * @param id 主键
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void remove(String id) {
        jobGroupMapper.remove(id);
    }

    /**
     * 获取注册列表
     *
     * @return List
     */
    @Override
    public List<JobRegistry> findJobRegistry() {
        List<JobRegistry> list = jobRegistryMapper.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
        return list;
    }

    /**
     * 通过主键查询 JobGroup
     *
     * @param id 主键
     * @return JobGroup
     */
    @Override
    public JobGroup queryById(String id) {
        return jobGroupMapper.queryById(id);
    }

    /**
     * 查询 JobInfo 表数量
     *
     * @param jobGroupId
     * @param triggerStatus
     * @param jobDesc
     * @param executorHandler
     * @return
     */
    @Override
    public int pageJobInfoListCount(String jobGroupId, int triggerStatus, String jobDesc, String executorHandler) {
        int count = jobInfoMapper.pageCount(jobGroupId, triggerStatus, jobDesc, executorHandler);
        return count;
    }

    /**
     * 查询所有
     *
     * @return List
     */
    @Override
    public List<JobGroup> findAll() {
        return jobGroupMapper.findAll();
    }


}
