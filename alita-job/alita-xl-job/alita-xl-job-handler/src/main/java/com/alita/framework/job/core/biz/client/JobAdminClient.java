package com.alita.framework.job.core.biz.client;

import com.alibaba.fastjson.JSON;
import com.alita.framework.job.core.biz.JobAdminExecutor;
import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.biz.model.RegistryParam;
import com.alita.framework.job.core.http.JobHttpClient;
import com.alita.framework.job.utils.StringUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JobAdminClient
 */
public class JobAdminClient implements JobAdminExecutor {

    /**
     * job管理端地址
     */
    private String addressUrl ;

    /**
     * token
     */
    private String accessToken;


    public JobAdminClient(String addressUrl) {
        Assert.notNull(addressUrl, "addressUrl must not be null");
        this.addressUrl = addressUrl;
    }

    /**
     * 执行器执行完任务后，回调任务结果时使用
     *
     * @param callbackParamList
     * [{
     *       "logId":1,              // 本次调度日志ID
     *       "logDateTim":0,         // 本次调度日志时间
     *       "handleCode":00000000,  // 00000000 表示任务执行正常，99999999表示失败
     *       "handleMsg": null
     * }]
     *
     * @return
     */
    @Override
    public String callback(List<HandleCallbackParam> callbackParamList) throws IOException {
        Map<String, String> headers = header();
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        String responseCode = jobHttpClient.post(addressUrl + "api/callback", JSON.toJSONString(callbackParamList), headers);

        return responseCode;
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
        Map<String, String> headers = header();
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        String responseCode = jobHttpClient.post(addressUrl + "api/registry", JSON.toJSONString(registryParam), headers);

        return responseCode;
    }

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    @Override
    public String registryRemove(RegistryParam registryParam) throws IOException {
        Map<String, String> headers = header();
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        String responseCode = jobHttpClient.post(addressUrl + "api/registryRemove", JSON.toJSONString(registryParam), headers);

        return responseCode;
    }


    private Map<String, String> header() {
        Map<String, String> headers = new HashMap<>(2);
        if (StringUtils.isNotBlank(this.accessToken)) {
            //headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            headers.put("JOB-ACCESS-TOKEN", this.accessToken);
        }
        headers.put(HttpHeaders.ACCEPT_CHARSET, ContentType.APPLICATION_JSON.toString());
        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }

        return headers;
    }

    @Override
    public String getAddressUrl() {
        return this.addressUrl;
    }
}
