package com.alita.framework.job.core.context;

public interface JobScheduleContext {

    /**
     * 初始化加载资源
     */
    void initialize();

    /**
     * 销毁系统资源
     */
    void destroy();

}
