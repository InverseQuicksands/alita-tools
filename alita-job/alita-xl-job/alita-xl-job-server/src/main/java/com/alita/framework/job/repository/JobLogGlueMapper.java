package com.alita.framework.job.repository;

import com.alita.framework.job.model.JobLogGlue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobLogGlueMapper {

    /**
     * 通过 jobId 查询 JobLogGlue 信息.
     *
     * @param jobId 主键
     * @return JobLogGlue
     */
    List<JobLogGlue> queryByJobId(String jobId);

    /**
     * 保存 JobLogGlue.
     *
     * @param jobLogGlue 信息
     */
    void save(JobLogGlue jobLogGlue);

    /**
     * 删除超过 30 天的旧数据
     *
     * @param jobId 主键
     * @param limit 天数
     */
    void removeOld(@Param("jobId") String jobId, @Param("limit") int limit);
}
