package com.alita.framework.job.model;

import java.io.Serializable;
import java.util.Date;

public class JobRegistry implements Serializable {

    private static final long serialVersionUID = 7431074993468564012L;

    /**
     * 主键
     */
    private String id;

    /**
     * 执行器分组（server or handler）
     */
    private String registryGroup;

    /**
     * 执行器AppName
     */
    private String registryKey;

    /**
     * 执行器地址，内置服务跟地址
     */
    private String registryValue;

    /**
     * 更新时间
     */
    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistryGroup() {
        return registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "JobRegistry{" +
                "id='" + id + '\'' +
                ", registryGroup='" + registryGroup + '\'' +
                ", registryKey='" + registryKey + '\'' +
                ", registryValue='" + registryValue + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
