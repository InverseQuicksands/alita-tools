package com.alita.framework.job.core.biz;

import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.biz.model.RegistryParam;

import java.io.IOException;
import java.util.List;

/**
 * JobServerExecutor
 */
public interface JobAdminExecutor {

    // ---------------------- callback ----------------------

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
    String callback(List<HandleCallbackParam> callbackParamList) throws IOException;


    // ---------------------- registry ----------------------

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
    String registry(RegistryParam registryParam) throws IOException;

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
    String registryRemove(RegistryParam registryParam) throws IOException;

    default String getAddressUrl() {
        return "";
    }

}
