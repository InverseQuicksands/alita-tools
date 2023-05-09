package com.alita.framework.job.core.executor.impl;

import com.alita.framework.job.core.executor.JobExecutor;
import com.alita.framework.job.core.glue.GlueFactory;
import com.alita.framework.job.core.handler.JobHandler;
import com.alita.framework.job.core.handler.ScheduleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Map;


public class JobSpringExecutor extends JobExecutor implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(JobSpringExecutor.class);

    private static ApplicationContext applicationContext;

    // ---------------------- applicationContext ----------------------
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JobSpringExecutor.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // start
    @Override
    public void afterSingletonsInstantiated() {
        // init JobHandler Repository (for method)
        initJobHandlerMethodRepository();
        // refresh GlueFactory
        GlueFactory.refreshInstance(1);

        // super start
        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // destroy
    @Override
    public void destroy() {
        super.destroy();
    }


    private void initJobHandlerMethodRepository() {
        if (applicationContext == null) {
            return;
        }
        // init job handler from method
        Map<String, Object> jobHandlerAnnotations = applicationContext.getBeansWithAnnotation(JobHandler.class);
        for (Map.Entry<String, Object> bean :jobHandlerAnnotations.entrySet()) {
            Object beanObj = bean.getValue();

            ReflectionUtils.doWithMethods(beanObj.getClass(), method -> {
                ScheduleJob scheduleJob = AnnotationUtils.findAnnotation(method, ScheduleJob.class);
                if (scheduleJob != null) {
                    // regist
                    registJobHandler(scheduleJob, beanObj, method);
                }
            });
        }
    }




}
