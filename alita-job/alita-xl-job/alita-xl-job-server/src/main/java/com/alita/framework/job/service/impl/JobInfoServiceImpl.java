package com.alita.framework.job.service.impl;

import com.alita.framework.job.common.ResponseStatus;
import com.alita.framework.job.core.exception.JobException;
import com.alita.framework.job.core.thread.JobTriggerPoolHelper;
import com.alita.framework.job.core.trigger.TriggerTypeEnum;
import com.alita.framework.job.core.thread.JobScheduleHelper;
import com.alita.framework.job.utils.DateUtils;
import com.alita.framework.job.common.AbstractServiceImpl;
import com.alita.framework.job.common.Response;
import com.alita.framework.job.dto.JobInfoDto;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.repository.JobInfoMapper;
import com.alita.framework.job.core.scheduler.ScheduleTypeEnum;
import com.alita.framework.job.service.JobInfoService;
import com.alita.framework.job.utils.I18nUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JobInfoServiceImpl extends AbstractServiceImpl implements JobInfoService {

    private static final Logger logger = LoggerFactory.getLogger(JobInfoServiceImpl.class);


    @Autowired
    private JobInfoMapper jobInfoMapper;



    /**
     * 任务管理列表
     *
     * @param jobInfoDto 参数
     * @return PageInfo
     */
    @Override
    public PageInfo<JobInfo> getJobList(JobInfoDto jobInfoDto) throws Exception {
        List<JobInfo> list = jobInfoMapper.getJobList(jobInfoDto);
        int count = jobInfoMapper.pageCount(jobInfoDto);

        return pageInfo(jobInfoDto, list, count);
    }

    /**
     * 新增任务
     *
     * @param jobInfo 任务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(JobInfo jobInfo) throws Exception {
        jobInfo.setId(String.valueOf(increment()));
        jobInfoMapper.add(jobInfo);
    }

    /**
     * 修改任务
     *
     * @param jobInfo 任务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(JobInfo jobInfo) throws Exception {
        jobInfoMapper.update(jobInfo);
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(String id) throws Exception {
        jobInfoMapper.remove(id);
    }

    /**
     * 停止任务
     *
     * @param jobInfo 任务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response stop(JobInfo jobInfo) throws Exception {
        JobInfo job = jobInfoMapper.queryById(jobInfo.getId());
        job.setTriggerStatus(0);
        job.setTriggerLastTime(0);
        job.setTriggerNextTime(0);
        job.setUpdateTime(new Date());
        jobInfoMapper.update(job);

        return Response.success();
    }

    /**
     * 开始任务
     *
     * @param jobInfo 任务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response start(JobInfo jobInfo) throws Exception {
        JobInfo job = jobInfoMapper.queryById(jobInfo.getId());

        // valid schedule type
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(job.getScheduleType(),
                ScheduleTypeEnum.NONE);

        if (ScheduleTypeEnum.NONE == scheduleTypeEnum) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("schedule_type_none_limit_start"));
        }

        // next trigger time (5s后生效，避开预读周期)
        long nextTriggerTime = 0;
        try {
            Date nextValidTime = JobScheduleHelper.generateNextValidTime(job, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
            if (nextValidTime == null) {
                return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), (I18nUtils.getProperty("schedule_type") + I18nUtils.getProperty("system_unvalid")) );
            }
            nextTriggerTime = nextValidTime.getTime();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), (I18nUtils.getProperty("schedule_type")+I18nUtils.getProperty("system_unvalid")) );
        }

        job.setTriggerStatus(1);
        job.setTriggerLastTime(0);
        job.setTriggerNextTime(nextTriggerTime);
        job.setUpdateTime(new Date());
        jobInfoMapper.update(job);

        return Response.success();
    }

    /**
     * 执行任务
     *
     * @param jobInfo
     * @param addressList 服务器地址列表
     * @return Response
     */
    @Override
    public Response trigger(JobInfo jobInfo, String addressList) throws Exception {
        if (StringUtils.isBlank(jobInfo.getExecutorParam())) {
            jobInfo.setExecutorParam("");
        }
        JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.MANUAL, -1, null, jobInfo.getExecutorParam(), addressList);
        return Response.success();
    }

    /**
     * 下次执行时间
     *
     * @param jobInfo 任务信息
     */
    @Override
    public List<String> nextTriggerTime(JobInfo jobInfo) throws Exception {
        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = JobScheduleHelper.generateNextValidTime(jobInfo, lastTime);
                if (lastTime != null) {
                    result.add(DateUtils.formatDateTime(lastTime, DateUtils.TIME_PATTERN));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JobException(e);
        }
        return result;
    }

    /**
     * 通过主键查询 JobInfo
     *
     * @param jobId 信息
     * @return JobInfo
     */
    @Override
    public JobInfo queryById(String jobId) {
        return jobInfoMapper.queryById(jobId);
    }

    /**
     * 通过 jobGroupId 查询 JobInfo 列表
     *
     * @param jobGroupId
     * @return List
     */
    @Override
    public List<JobInfo> getJobsByGroup(String jobGroupId) {
        return jobInfoMapper.getJobsByGroup(jobGroupId);
    }
}
