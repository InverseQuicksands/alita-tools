package com.alita.framework.job.repository;

import com.alita.framework.job.model.JobRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface JobRegistryMapper {

    /**
     * 查询是否有心跳超时的注册执行器.
     *
     * @param timeout 超时时间 90s
     * @param nowTime 当前时间
     * @return List
     */
    List<Integer> findDeadHandler(@Param("timeout") int timeout,
                                  @Param("nowTime") Date nowTime);

    /**
     * 移除心跳超时的注册器.
     *
     * @param ids 超时执行器集合
     * @return null
     */
    int removeDeadHandler(@Param("ids") List<Integer> ids);

    /**
     * 查询所有未超时的执行器集合
     *
     * @param timeout 超时时间 90s
     * @param nowTime 当前时间
     * @return List
     */
    List<JobRegistry> findAll(@Param("timeout") int timeout,
                              @Param("nowTime") Date nowTime);


    int registryDelete(@Param("registryGroup") String registryGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue);

    int registrySave(@Param("id") String id,
                     @Param("registryGroup") String registryGroup,
                     @Param("registryKey") String registryKey,
                     @Param("registryValue") String registryValue,
                     @Param("updateTime") Date updateTime);

    int registryUpdate(@Param("registryGroup") String registryGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue,
                       @Param("updateTime") Date updateTime);
}
