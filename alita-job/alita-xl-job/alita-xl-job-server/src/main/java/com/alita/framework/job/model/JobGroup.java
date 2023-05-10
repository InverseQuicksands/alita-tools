package com.alita.framework.job.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JobGroup implements Serializable {

    private static final long serialVersionUID = -1699302943794304001L;

    /**
     * 主键
     */
    private String id;

    /**
     * 执行器AppName
     */
    private String appName;

    /**
     * 执行器名称
     */
    private String title;

    /**
     * 执行器地址类型：0-自动注册,1-手动录入
     */
    private int addressType;

    /**
     * 执行器地址列表，多地址逗号分隔
     */
    private String addressList;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 执行器地址列表(系统注册)
     */
    private List<String> registryList;

    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "JobGroup{" +
                "id='" + id + '\'' +
                ", appName='" + appName + '\'' +
                ", title='" + title + '\'' +
                ", addressType='" + addressType + '\'' +
                ", addressList='" + addressList + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
