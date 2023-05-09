package com.alita.framework.job.repository;

import com.alita.framework.job.model.JobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface JobLogMapper {

    /**
     * 保存 jobLog
     *
     * @param jobLog
     */
    int save(JobLog jobLog);

    /**
     * 获取失败 job 日志主键
     *
     * @param pagesize 分页大小
     * @return 所有失败 job 日志主键
     */
    List<String> findFailJobLogIds(@Param("pagesize") int pagesize);

    /**
     * 修改告警状态
     *
     * @param failJobLogId job_log 主键
     * @param oldAlarmStatus 旧状态
     * @param newAlarmStatus 新状态
     */
    int updateAlarmStatus(String failJobLogId, int oldAlarmStatus, int newAlarmStatus);

    /**
     * 通过主键查询指定 job log.
     *
     * @param failJobLogId job_log 主键
     * @return JobLog
     */
    JobLog queryById(@Param("id") String failJobLogId);

    /**
     * 修改 job_log 表
     *
     * @param jobLog 信息
     */
    int updateTriggerInfo(JobLog jobLog);

    /**
     * 查询【调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线】.
     *
     * @param losedTime 超时时间
     * @return List
     */
    List<String> findLostJobIds(@Param("losedTime") Date losedTime);

    /**
     * 修改 joblog 信息.
     *
     * @param jobLog 信息
     */
    int updateHandleInfo(JobLog jobLog);

    Map<String, Object> findLogReport(@Param("from") Date from,
                                      @Param("to") Date to);

    List<Long> findClearLogIds(@Param("jobGroup") int jobGroup,
                               @Param("jobId") int jobId,
                               @Param("clearBeforeTime") Date clearBeforeTime,
                               @Param("clearBeforeNum") int clearBeforeNum,
                               @Param("pagesize") int pagesize);

    /**
     * 清理日志表
     *
     * @param logIds 主键
     */
    void clearLog(List<Long> logIds);


    List<JobLog> pageList(@Param("offset") int offset,
                          @Param("pagesize") int pagesize,
                          @Param("jobGroup") String jobGroup,
                          @Param("jobId") String jobId,
                          @Param("triggerTimeStart") Date triggerTimeStart,
                          @Param("triggerTimeEnd") Date triggerTimeEnd,
                          @Param("logStatus") int logStatus);

    int pageListCount(@Param("jobGroup") String jobGroup,
                      @Param("jobId") String jobId,
                      @Param("triggerTimeStart") Date triggerTimeStart,
                      @Param("triggerTimeEnd") Date triggerTimeEnd,
                      @Param("logStatus") int logStatus);}
