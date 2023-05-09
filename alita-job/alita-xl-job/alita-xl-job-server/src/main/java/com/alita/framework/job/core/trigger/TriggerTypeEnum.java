package com.alita.framework.job.core.trigger;


import com.alita.framework.job.utils.I18nUtils;

/**
 * 触发器类类型
 */
public enum TriggerTypeEnum {

    /**
     * 手动触发
     */
    MANUAL(I18nUtils.getProperty("jobconf_trigger_type_manual")),
    /**
     * Cron触发
     */
    CRON(I18nUtils.getProperty("jobconf_trigger_type_cron")),
    /**
     * 失败重试触发
     */
    RETRY(I18nUtils.getProperty("jobconf_trigger_type_retry")),
    /**
     * 父任务触发
     */
    PARENT(I18nUtils.getProperty("jobconf_trigger_type_parent")),
    /**
     * API触发
     */
    API(I18nUtils.getProperty("jobconf_trigger_type_api")),
    /**
     * 调度过期补偿
     */
    MISFIRE(I18nUtils.getProperty("jobconf_trigger_type_misfire"));

    private String title;

    TriggerTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
