package com.alita.framework.job.core.scheduler;

import com.alita.framework.job.utils.I18nUtils;

/**
 * 过期策略
 */
public enum MisfireStrategyEnum {

    /**
     * 忽略
     */
    DO_NOTHING(I18nUtils.getProperty("misfire_strategy_do_nothing")),

    /**
     * 立即执行一次
     */
    FIRE_ONCE_NOW(I18nUtils.getProperty("misfire_strategy_fire_once_now"));

    private String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /**
     * 匹配失效策略.
     *
     * @param name 传入策略
     * @param defaultMisfireStrategyEnum 默认策略
     * @return MisfireStrategyEnum
     */
    public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultMisfireStrategyEnum) {
        for (MisfireStrategyEnum misfireStrategyEnum: MisfireStrategyEnum.values()) {
            if (misfireStrategyEnum.name().equals(name)) {
                return misfireStrategyEnum;
            }
        }
        return defaultMisfireStrategyEnum;
    }

}
