package com.alita.framework.job.core.executor.impl;

import com.alita.framework.job.core.executor.JobExecutor;
import com.alita.framework.job.core.handler.ScheduleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * JobSimpleExecutor
 */
public class JobSimpleExecutor extends JobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JobSimpleExecutor.class);


    private List<Object> jobBeanList = new ArrayList<>();

    public List<Object> getJobBeanList() {
        return jobBeanList;
    }
    public void setJobBeanList(List<Object> jobBeanList) {
        this.jobBeanList = jobBeanList;
    }


    @Override
    public void start() {

        // init JobHandler Repository (for method)
        initJobHandlerMethodRepository(jobBeanList);

        // super start
        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    private void initJobHandlerMethodRepository(List<Object> jobBeanList) {
        if (jobBeanList==null || jobBeanList.size()==0) {
            return;
        }

        // init job handler from method
        for (Object bean: jobBeanList) {
            // method
            Method[] methods = bean.getClass().getDeclaredMethods();
            if (methods.length == 0) {
                continue;
            }
            for (Method executeMethod : methods) {
                ScheduleJob scheduleJob = executeMethod.getAnnotation(ScheduleJob.class);
                // registry
                registJobHandler(scheduleJob, bean, executeMethod);
            }

        }

    }

}
