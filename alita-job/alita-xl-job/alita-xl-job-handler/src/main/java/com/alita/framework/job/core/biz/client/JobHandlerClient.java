package com.alita.framework.job.core.biz.client;

import com.alibaba.fastjson.JSON;
import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.biz.model.LogParam;
import com.alita.framework.job.core.biz.model.LogResult;
import com.alita.framework.job.core.biz.model.TriggerParam;
import com.alita.framework.job.core.http.JobHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JobHandlerClient implements JobHandlerExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JobHandlerClient.class);

    /**
     * 执行器地址
     */
    private String address;


    public JobHandlerClient(String address) {
        Assert.notNull(address, "address must not be null");
        this.address = address;
    }

    /**
     * 是否空闲
     *
     * @param executorParam 执行参数
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    @Override
    public String idleBeat(ExecutorParam executorParam) throws IOException {
        Map<String, String> headers = header();
        // 发送至执行器内嵌服务，地址格式：{执行器内嵌服务根地址}/idleBeat
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        String responseCode = jobHttpClient.post(address + "idleBeat", JSON.toJSONString(executorParam), headers);

        return responseCode;
    }

    /**
     * 心跳检测
     *
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    @Override
    public String beat() throws IOException {
        Map<String, String> headers = header();
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        // 发送至执行器内嵌服务，地址格式：{执行器内嵌服务根地址}/beat
        String responseCode = jobHttpClient.post(address + "beat", null, headers);

        return responseCode;
    }

    /**
     * 运行任务
     *
     * @param triggerParam 执行参数
     */
    @Override
    public String run(TriggerParam triggerParam) throws IOException {
        Map<String, String> headers = header();
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        // 发送至执行器内嵌服务，地址格式：{执行器内嵌服务根地址}/run
        String responseCode = jobHttpClient.post(address + "run", JSON.toJSONString(triggerParam), headers);
        return responseCode;
    }

    /**
     * 关闭任务
     *
     * @param killParam 执行参数
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    @Override
    public String kill(ExecutorParam killParam) throws IOException {
        Map<String, String> headers = header();
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        // 发送至执行器内嵌服务，地址格式：{执行器内嵌服务根地址}/kill
        String responseCode = jobHttpClient.post(address + "kill", JSON.toJSONString(killParam), headers);

        return responseCode;
    }

    /**
     * log
     *
     * @param logParam
     * @return LogResult
     */
    @Override
    public LogResult log(LogParam logParam) throws IOException {
        Map<String, String> headers = header();
        JobHttpClient jobHttpClient = JobHttpClient.getInstance();
        // 发送至执行器内嵌服务，地址格式：{执行器内嵌服务根地址}/log
        LogResult logResult = jobHttpClient.post(address + "log", JSON.toJSONString(logParam), headers);

        return logResult;
    }


    private Map<String, String> header() {
        Map<String, String> headers = new HashMap<>(2);
        headers.put(HttpHeaders.ACCEPT_CHARSET, ContentType.APPLICATION_JSON.toString());
        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        if (!this.address.endsWith("/")) {
            this.address = this.address + "/";
        }

        return headers;
    }


}
