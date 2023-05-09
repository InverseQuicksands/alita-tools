/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alita.framework.platform.autoconfigure.data.jdbc;

import com.google.common.base.MoreObjects;

/**
 * <Description>
 * ...
 *
 * @author Zhang Liang
 * @date 2020-06-25 11:14
 * @Version V1.0
 * @since 1.8
 */

public class PlatformDataBaseMetaInfo {

    /** 数据库类型 */
    private String databaseType;

    /** 数据库产品名称 */
    private String productName;

    /** 数据库版本号 */
    private String productVersion;

    /** 数据库主版本号 */
    private int databaseMajorVersion;

    /** 数据库次版本号 */
    private int databaseMinorVersion;

    /** 驱动名称 */
    private String driverName;

    /** 驱动版本号 */
    private String driverVersion;

    /** 驱动主版本号 */
    private int driverMajorVersion;

    /** 驱动次版本号 */
    private int driverMinorVersion;

    /** 默认事务级别 */
    private int defaultTransactionIsolation;

    /** 是否只读 */
    private boolean isReadOnly;

    public String getDatabaseType() {
        return databaseType;
    }

    public PlatformDataBaseMetaInfo setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public PlatformDataBaseMetaInfo setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public PlatformDataBaseMetaInfo setProductVersion(String productVersion) {
        this.productVersion = productVersion;
        return this;
    }

    public int getDatabaseMajorVersion() {
        return databaseMajorVersion;
    }

    public PlatformDataBaseMetaInfo setDatabaseMajorVersion(int databaseMajorVersion) {
        this.databaseMajorVersion = databaseMajorVersion;
        return this;
    }

    public int getDatabaseMinorVersion() {
        return databaseMinorVersion;
    }

    public PlatformDataBaseMetaInfo setDatabaseMinorVersion(int databaseMinorVersion) {
        this.databaseMinorVersion = databaseMinorVersion;
        return this;
    }

    public String getDriverName() {
        return driverName;
    }

    public PlatformDataBaseMetaInfo setDriverName(String driverName) {
        this.driverName = driverName;
        return this;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public PlatformDataBaseMetaInfo setDriverVersion(String driverVersion) {
        this.driverVersion = driverVersion;
        return this;
    }

    public int getDriverMajorVersion() {
        return driverMajorVersion;
    }

    public PlatformDataBaseMetaInfo setDriverMajorVersion(int driverMajorVersion) {
        this.driverMajorVersion = driverMajorVersion;
        return this;
    }

    public int getDriverMinorVersion() {
        return driverMinorVersion;
    }

    public PlatformDataBaseMetaInfo setDriverMinorVersion(int driverMinorVersion) {
        this.driverMinorVersion = driverMinorVersion;
        return this;
    }

    public int getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public PlatformDataBaseMetaInfo setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        return this;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public PlatformDataBaseMetaInfo setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
        return this;
    }

    public String out() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n#############【 数据库基本信息 】###########:\n");
        builder.append("**********【 数据库类型 】**********:" + this.getDatabaseType() + "\n");
        builder.append("**********【 数据库产品名称 】*******:" + this.getProductName() + "\n");
        builder.append("**********【 数据库版本号 】*********:" + this.getProductVersion() + "\n");
        builder.append("**********【 数据库主版本号 】*******:" + this.getDatabaseMajorVersion() + "\n");
        builder.append("**********【 数据库次版本号 】*******:" + this.getDatabaseMinorVersion() + "\n");
        builder.append("**********【 驱动名称 】************:" + this.getDriverName() + "\n");
        builder.append("**********【 驱动版本号 】**********:" + this.getDriverVersion() + "\n");
        builder.append("**********【 驱动主版本号 】*********:" + this.getDriverMajorVersion() + "\n");
        builder.append("**********【 驱动次版本号 】*********:" + this.getDriverMinorVersion() + "\n");
        builder.append("**********【 默认事务级别 】*********:" + this.getDefaultTransactionIsolation() + "\n");
        builder.append("**********【 是否只读 】************:" + this.isReadOnly());
        return builder.toString();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("databaseType", getDatabaseType())
                .add("productName", getProductName())
                .add("productVersion", getProductVersion())
                .add("databaseMajorVersion", getDatabaseMajorVersion())
                .add("databaseMinorVersion", getDatabaseMinorVersion())
                .add("driverName", getDriverName())
                .add("driverVersion", getDriverVersion())
                .add("driverMajorVersion", getDriverMajorVersion())
                .add("driverMinorVersion", getDriverMinorVersion())
                .add("defaultTransactionIsolation", getDefaultTransactionIsolation())
                .add("isReadOnly",isReadOnly())
                .toString();
    }

}
