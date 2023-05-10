package com.alita.framework.job.repository;

import com.alita.framework.job.dto.JobInfoDto;
import com.alita.framework.job.model.JobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobInfoMapper {

    /**
     * 任务管理列表
     *
     * @param jobInfoDto 参数
     * @return PageInfo
     */
    List<JobInfo> getJobList(JobInfoDto jobInfoDto);

    /**
     * 调度任务查询
     *
     * @param maxNextTime 最大下次时间
     * @param pagesize 预读取数
     * @return 调度任务列表
     */
    List<JobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize);

    int scheduleUpdate(JobInfo jobInfo);

    /**
     * 获取符合条件的任务总数
     *
     * @param jobInfoDto 参数
     * @return 总数
     */
    int pageCount(JobInfoDto jobInfoDto);

    /**
     * 通过主键查询 JobInfo
     *
     * @param id 主键
     * @return JobInfo
     */
    JobInfo queryById(String id);

    /**
     * 新增任务
     *
     * @param jobInfo 任务信息
     */
    void add(JobInfo jobInfo);

    /**
     * 修改任务
     *
     * @param jobInfo 任务信息
     */
    void update(JobInfo jobInfo);

    /**
     * 删除任务
     *
     * @param id 任务id
     */
    void remove(@Param("id") String id);


    List<JobInfo> getJobsByGroup(String jobGroupId);

    int pageCount(@Param("jobGroup") String jobGroup,
                  @Param("triggerStatus") int triggerStatus,
                  @Param("jobDesc") String jobDesc,
                  @Param("executorHandler") String executorHandler);
}
