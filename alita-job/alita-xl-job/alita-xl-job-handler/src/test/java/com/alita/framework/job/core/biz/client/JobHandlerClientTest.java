package com.alita.framework.job.core.biz.client;

import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.biz.model.LogParam;
import com.alita.framework.job.core.biz.model.TriggerParam;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * JobHandlerClientTest
 *
 * @date 2023-01-31 11:17
 */
@DisplayName("JobHandlerClientTest")
public class JobHandlerClientTest {

    private JobHandlerClient client = new JobHandlerClient("http://127.0.0.1:8100/");

    @Test
    public void idleBeat() {
        ExecutorParam executorParam = new ExecutorParam();
        executorParam.setJobId("11111111");

        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.idleBeat(executorParam));
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }


    @Test
    public void beat() {
        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.beat());
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }


    @Test
    public void run() {
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId("11111111");

        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.run(triggerParam));
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }


    @Test
    public void kill() {
        ExecutorParam executorParam = new ExecutorParam();
        executorParam.setJobId("11111111");

        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.kill(executorParam));
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }


    @Test
    public void log() {
        LogParam logParam = new LogParam();
        logParam.setLogId(11111111);

        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.log(logParam));
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }





}
