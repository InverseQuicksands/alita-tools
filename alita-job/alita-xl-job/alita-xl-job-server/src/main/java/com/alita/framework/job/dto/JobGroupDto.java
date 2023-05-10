package com.alita.framework.job.dto;

import com.alita.framework.job.common.AbstractQueryPage;

import java.io.Serializable;

/**
 * JobGroupDto
 *
 * @date 2022-12-26 12:49
 */
public class JobGroupDto extends AbstractQueryPage implements Serializable {

    private static final long serialVersionUID = -3765691803100638415L;

    private String id;

    private String appName;

    private String title;

    private int addressType;

    private String addressList;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "JobGroupDto{" +
                "id='" + id + '\'' +
                ", appName='" + appName + '\'' +
                ", title='" + title + '\'' +
                ", addressType=" + addressType +
                ", addressList='" + addressList + '\'' +
                "} " + super.toString();
    }
}
