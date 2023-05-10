package com.alita.framework.job.core.biz.impl;

import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.biz.model.TriggerParam;
import com.alita.framework.job.core.glue.GlueTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * JobHandlerExecutorImplTest
 *
 * @date 2023-01-31 11:24
 */
@DisplayName("JobHandlerExecutorImplTest")
public class JobHandlerExecutorImplTest {

    private JobHandlerExecutorImpl jobHandlerExecutor = new JobHandlerExecutorImpl();

    @Test
    public void idleBeat() throws IOException {
        ExecutorParam param = new ExecutorParam();
        param.setJobId("11111111");

        String result = jobHandlerExecutor.idleBeat(param);
        Assertions.assertEquals("00000000", result);
    }


    @Test
    public void beat() throws IOException {
        String result = jobHandlerExecutor.beat();
        Assertions.assertEquals("00000000", result);
    }


    @Test
    public void run() throws IOException {
        TriggerParam param = new TriggerParam();
        param.setJobId("11111111");
        param.setGlueType(GlueTypeEnum.BEAN.name());
        param.setExecutorHandler("appname");

        String result = jobHandlerExecutor.run(param);
        Assertions.assertEquals("99999999", result);
    }


    @Test
    public void kill() throws IOException {
        ExecutorParam param = new ExecutorParam();
        param.setJobId("11111111");

        String result = jobHandlerExecutor.kill(param);
        Assertions.assertEquals("00000000", result);
    }

}
