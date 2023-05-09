package com.alita.framework.job.repository;

import com.alita.framework.job.model.JobGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobGroupMapper {

    /**
     * 通过分组主键查询 jobGroup.
     *
     * @param id 主键
     * @return JobGroup
     */
    JobGroup queryById(@Param("id") String id);

    /**
     * 通过获取执行器地址类型查询所有job组信息.
     *
     * @param addressType 执行器地址类型：0-自动注册,1-手动录入
     * @return List
     */
    List<JobGroup> findByAddressType(@Param("addressType") int addressType);

    /**
     * 更新 JobGroup.
     *
     * @param jobGroup 信息
     * @return int
     */
    int update(JobGroup jobGroup);

    /**
     * 保存 JobGroup.
     *
     * @param jobGroup 信息
     * @return int
     */
    int save(JobGroup jobGroup);

    /**
     * 分页查询
     *
     * @param offset 起始页
     * @param pagesize 分页大小
     * @param appName 执行器
     * @param title 执行器名称
     * @return
     */
    List<JobGroup> pageList(@Param("offset") int offset,
                            @Param("pagesize") int pagesize,
                            @Param("appName") String appName,
                            @Param("title") String title);

    /**
     * 统计数量
     *
     * @param appName 执行器
     * @param title 执行器名称
     * @return 数量
     */
    int pageListCount(String appName, String title);

    /**
     * 删除 JobGroup
     *
     * @param id 主键
     * @return int
     */
    int remove(String id);

    /**
     * 查询所有 JobGroup 数据
     *
     * @return List
     */
    List<JobGroup> findAll();

}
