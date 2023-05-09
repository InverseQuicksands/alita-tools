package com.alita.framework.job.core.scheduler;


/**
 * 调度状态枚举类
 */
public enum ScheduleTypeEnum {

    NONE("无"),

    /**
     * schedule by cron
     */
    CRON("cron"),

    /**
     * schedule by fixed rate (in seconds)
     */
    FIX_RATE("固定速度");

    private String title;

    ScheduleTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem){
        for (ScheduleTypeEnum item: ScheduleTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
