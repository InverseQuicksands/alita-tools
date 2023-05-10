package com.alita.framework.job.core.biz.client;

import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.biz.model.RegistryParam;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * JobAdminClientTest
 *
 * @date 2023-01-31 10:41
 */
@DisplayName("JobAdminClientTest")
public class JobAdminClientTest {

    private JobAdminClient client = new JobAdminClient("http://127.0.0.1:8100/");

    @Test
    public void callback() {
        List<HandleCallbackParam> list = new ArrayList<>(1);
        HandleCallbackParam handleCallbackParam = new HandleCallbackParam();
        handleCallbackParam.setLogId(1);
        list.add(handleCallbackParam);
        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.callback(list));
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }

    @Test
    public void registry() {
        RegistryParam registryParam = new RegistryParam();
        registryParam.setRegistryGroup("appname");
        registryParam.setRegistryKey("appname");

        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.registry(registryParam));
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }


    @Test
    public void registryRemove() {
        RegistryParam registryParam = new RegistryParam();
        registryParam.setRegistryGroup("appname");
        registryParam.setRegistryKey("appname");

        HttpHostConnectException exception = Assertions.assertThrows(HttpHostConnectException.class,
                () -> client.registryRemove(registryParam));
        Assertions.assertTrue(exception.getMessage().contains("Connection refused"));
    }

}
