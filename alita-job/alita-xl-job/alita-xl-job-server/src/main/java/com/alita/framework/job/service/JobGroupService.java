package com.alita.framework.job.service;

import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.dto.JobGroupDto;
import com.alita.framework.job.model.JobGroup;
import com.alita.framework.job.model.JobRegistry;

import java.util.List;

/**
 * JobGroupService
 *
 * @date 2022-12-26 11:05
 */
public interface JobGroupService {

    PageInfo<JobGroup> pageList(JobGroupDto jobGroupDto);

    void save(JobGroup jobGroup);

    void update(JobGroup jobGroup);

    List<JobRegistry> findJobRegistry();

    JobGroup queryById(String id);

    int pageJobInfoListCount(String jobGroupId, int triggerStatus, String jobDesc, String executorHandler);

    List<JobGroup> findAll();

    void remove(String id);
}
