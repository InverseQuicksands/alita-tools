package com.alita.framework.job.core.executor;

/**
 * JobExecutorParam
 *
 * @date 2022-12-02 23:45
 */
public class JobExecutorParam {

    private static class JobExecutorParamSington {
        public static final JobExecutorParam instance = new JobExecutorParam();
    }

    private JobExecutorParam() {

    }

    public static JobExecutorParam getInstance() {
        return JobExecutorParamSington.instance;
    }

    /**
     * 管理端地址
     */
    private String adminAddresses;

    /**
     * token
     */
    private String accessToken;

    /**
     * 执行器的appname
     */
    private String appName;

    /**
     * 注册地址，优先使用该配置作为注册地址
     */
    private String address;

    /**
     * 为空时使用内嵌服务 ”IP:PORT“ 作为注册地址. 从而更灵活的支持容器类型执行器动态IP和动态映射端口问题
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * 日志路径
     */
    private String logPath;

    /**
     * 日志保留天数
     */
    private int logRetentionDays;


    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public int getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    @Override
    public String toString() {
        return "JobExecutorParam{" +
                "adminAddresses='" + adminAddresses + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", appName='" + appName + '\'' +
                ", address='" + address + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", logPath='" + logPath + '\'' +
                ", logRetentionDays=" + logRetentionDays +
                '}';
    }
}
