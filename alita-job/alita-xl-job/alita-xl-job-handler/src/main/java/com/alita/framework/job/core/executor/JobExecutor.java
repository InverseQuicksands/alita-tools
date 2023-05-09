package com.alita.framework.job.core.executor;

import com.alita.framework.job.core.biz.JobAdminExecutor;
import com.alita.framework.job.core.biz.client.JobAdminClient;
import com.alita.framework.job.core.handler.IJobHandler;
import com.alita.framework.job.core.handler.ScheduleJob;
import com.alita.framework.job.core.handler.impl.MethodJobHandler;
import com.alita.framework.job.core.http.EmbedServer;
import com.alita.framework.job.core.thread.JobLogFileCleanThread;
import com.alita.framework.job.core.thread.JobThread;
import com.alita.framework.job.core.thread.TriggerCallbackThread;
import com.alita.framework.job.utils.IpUtils;
import com.alita.framework.job.utils.NetUtil;
import com.alita.framework.job.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JobExecutor job执行器.
 *
 * <p>
 * job-handler 的核心类。负责接受 server 发送的请求及处理具体的任务.
 */
public class JobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    // ---------------------- param ----------------------
    private String adminAddresses;
    private String accessToken;
    private String appname;
    private String address;
    private String ip;
    private int port;
    private String logPath;
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

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
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

    // ---------------------- start + stop ----------------------

    private final JobExecutorParam param = JobExecutorParam.getInstance();
    private static List<JobAdminExecutor> adminBizList;
    private EmbedServer embedServer = null;
    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();
    private static ConcurrentMap<String, JobThread> jobThreadRepository = new ConcurrentHashMap<String, JobThread>();

    public static List<JobAdminExecutor> getAdminBizList(){
        return adminBizList;
    }

    /**
     * 初始化
     *
     * @throws Exception
     */
    public void start() throws Exception {

        // init logpath
        JobFileAppender.initLogPath(param.getLogPath());

        // init invoker, admin-client
        initAdminBizList(param.getAdminAddresses(), param.getAccessToken());

        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(param.getLogRetentionDays());

        // init TriggerCallbackThread
        TriggerCallbackThread.getInstance().start();

        // init executor-server
        initEmbedServer(param.getAddress(), param.getIp(), param.getPort(), param.getAppName(), param.getAccessToken());
    }


    /**
     * 关闭
     */
    public void destroy(){
        // destroy executor-server
        stopEmbedServer();

        // destroy jobThreadRepository
        if (jobThreadRepository.size() > 0) {
            for (Map.Entry<String, JobThread> item: jobThreadRepository.entrySet()) {
                JobThread oldJobThread = removeJobThread(item.getKey(), "web container destroy and kill the job.");
                // wait for job thread push result to callback queue
                if (oldJobThread != null) {
                    try {
                        oldJobThread.join();
                    } catch (InterruptedException e) {
                        logger.error(">>>>>>>>>>> JobThread destroy(join) error, jobId:{}", item.getKey(), e);
                    }
                }
            }
        }
        jobHandlerRepository.clear();

        // destroy JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();

        // destroy TriggerCallbackThread
        TriggerCallbackThread.getInstance().toStop();
    }


    // ---------------------- admin-client (rpc invoker) ----------------------
    private void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
        if (StringUtils.isBlank(adminAddresses)) {
            return;
        }
        String[] addressArray = adminAddresses.trim().split(",");
        for (String address: addressArray) {
            if (StringUtils.isNotBlank(address)) {
                JobAdminExecutor adminBiz = new JobAdminClient(address.trim());
                if (adminBizList == null) {
                    adminBizList = new ArrayList<JobAdminExecutor>();
                }
                adminBizList.add(adminBiz);
            }
        }
    }


    // ---------------------- executor-server (rpc provider) ----------------------
    private void initEmbedServer(String address, String ip, int port, String appname, String accessToken) throws Exception {

        // fill ip port
        if (port <= 0) {
            port = NetUtil.findAvailablePort(9999);
        }

        if (StringUtils.isBlank(ip)) {
            ip = IpUtils.getIp();
        }

        // generate address
        if (StringUtils.isBlank(address)) {
            // registry-address：default use address to registry , otherwise use ip:port if address is null
            String ip_port_address = IpUtils.getIpPort(ip, port);
            address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
        }

        // accessToken
        if (StringUtils.isBlank(accessToken)) {
            logger.warn(">>>>>>>>>>> job accessToken is empty. To ensure system security, please set the accessToken.");
        }

        // start
        embedServer = new EmbedServer();
        embedServer.start(address, port, appname, accessToken);
    }

    private void stopEmbedServer() {
        // stop provider factory
        if (embedServer != null) {
            try {
                embedServer.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    // ---------------------- job handler repository ----------------------

    public static IJobHandler loadJobHandler(String name){
        return jobHandlerRepository.get(name);
    }

    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler){
        logger.info(">>>>>>>>>>> job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    protected void registJobHandler(ScheduleJob scheduleJob, Object bean, Method executeMethod){
        if (scheduleJob == null) {
            return;
        }

        String name = scheduleJob.value();
        // make and simplify the variables since they'll be called several times later
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("job method-jobhandler name invalid, for[" + clazz + "#" + methodName + "] .");
        }
        if (loadJobHandler(name) != null) {
            throw new RuntimeException("job jobhandler[" + name + "] naming conflicts.");
        }

        executeMethod.setAccessible(true);

        // init and destroy
        Method initMethod = null;
        Method destroyMethod = null;

        if (StringUtils.isNotBlank(scheduleJob.init())) {
            try {
                initMethod = clazz.getDeclaredMethod(scheduleJob.init());
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("job method-jobhandler initMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        if (StringUtils.isNotBlank(scheduleJob.destroy())) {
            try {
                destroyMethod = clazz.getDeclaredMethod(scheduleJob.destroy());
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("job method-jobhandler destroyMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }

        // registry jobhandler
        registJobHandler(name, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));
    }


    // ---------------------- job thread repository ----------------------
    public static JobThread registJobThread(String jobId, IJobHandler handler, String removeOldReason){
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public static JobThread removeJobThread(String jobId, String removeOldReason){
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();

            return oldJobThread;
        }
        return null;
    }

    public static JobThread loadJobThread(String jobId){
        return jobThreadRepository.get(jobId);
    }

}
