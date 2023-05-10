package com.alita.framework.job.service;

import com.alita.framework.job.common.Response;
import com.alita.framework.job.dto.JobInfoDto;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.common.PageInfo;

import java.util.List;

public interface JobInfoService {

    /**
     * 任务管理列表
     *
     * @param jobInfoDto 参数
     * @return PageInfo
     * @throws Exception
     */
    PageInfo<JobInfo> getJobList(JobInfoDto jobInfoDto) throws Exception;

    /**
     * 新增任务
     *
     * @param jobInfo 任务信息
     * @throws Exception
     */
    void add(JobInfo jobInfo) throws Exception;

    /**
     * 修改任务
     *
     * @param jobInfo 任务信息
     * @throws Exception
     */
    void update(JobInfo jobInfo) throws Exception;

    /**
     * 删除任务
     *
     * @param id 任务id
     * @throws Exception
     */
    void remove(String id) throws Exception;

    /**
     * 停止任务
     *
     * @param jobInfo 任务信息
     * @throws Exception
     */
    Response stop(JobInfo jobInfo) throws Exception;

    /**
     * 开始任务
     *
     * @param jobInfo 任务信息
     * @throws Exception
     */
    Response start(JobInfo jobInfo) throws Exception;

    /**
     * 执行任务
     *
     * @param jobInfo 任务信息
     * @param addressList 服务器地址列表
     * @return Response
     * @throws Exception
     */
    Response trigger(JobInfo jobInfo, String addressList) throws Exception;

    /**
     * 下次执行时间
     *
     * @param jobInfo 任务信息
     * @return List 下次执行时间列表
     * @throws Exception
     */
    List<String> nextTriggerTime(JobInfo jobInfo) throws Exception;

    /**
     * 通过主键查询 JobInfo
     *
     * @param jobId 信息
     * @return JobInfo
     */
    JobInfo queryById(String jobId);

    List<JobInfo> getJobsByGroup(String jobGroupId);
}
