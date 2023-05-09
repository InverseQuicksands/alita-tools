package com.alita.framework.job.service.impl;

import com.alita.framework.job.common.Response;
import com.alita.framework.job.common.ResponseStatus;
import com.alita.framework.job.core.biz.JobAdminExecutor;
import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.biz.model.RegistryParam;
import com.alita.framework.job.core.thread.JobCompleteHelper;
import com.alita.framework.job.core.thread.JobRegistryHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * JobAdminExecutorImpl
 *
 * @date 2022-12-14 14:04
 */
@Service
public class JobAdminExecutorImpl implements JobAdminExecutor {


    /**
     * 执行器执行完任务后，回调任务结果时使用。
     *
     * @param callbackParamList
     * [{
     *    "logId":1,              // 本次调度日志ID
     *    "logDateTim":0,         // 本次调度日志时间
     *    "handleCode":00000000,  // 00000000 表示任务执行正常，99999999表示失败
     *    "handleMsg": null
     * }]
     * @return 00000000-成功，99999999-失败
     */
    @Override
    public String callback(List<HandleCallbackParam> callbackParamList) throws IOException {
        JobCompleteHelper.getInstance().callback(callbackParamList);
        return ResponseStatus.SUCCESS.getCode();
    }

    /**
     * 执行器注册时使用，调度中心会实时感知注册成功的执行器并发起任务调度.
     *
     * @param registryParam
     * {
     *     "registryGroup":"EXECUTOR",                     // 固定值
     *     "registryKey":"job-executor-example",       // 执行器AppName
     *     "registryValue":"http://127.0.0.1:9999/"        // 执行器地址，内置服务跟地址
     * }
     * @return 00000000-成功，99999999-失败
     */
    @Override
    public String registry(RegistryParam registryParam) throws IOException {
        Response response = JobRegistryHelper.getInstance().registry(registryParam);
        return response.getCode();
    }

    /**
     * 执行器注册摘除时使用，注册摘除后的执行器不参与任务调度与执行
     *
     * @param registryParam
     * {
     *     "registryGroup":"EXECUTOR",                 // 固定值
     *     "registryKey":"job-executor-example",       // 执行器AppName
     *     "registryValue":"http://127.0.0.1:9999/"    // 执行器地址，内置服务跟地址
     * }
     *
     * @return 00000000-成功，99999999-失败
     */
    @Override
    public String registryRemove(RegistryParam registryParam) throws IOException {
        Response response = JobRegistryHelper.getInstance().registryRemove(registryParam);
        return response.getCode();
    }
}
