package com.alita.framework.job.core.context;

/**
 * job context
 *
 */
public class JobContext {

    public static final String HANDLE_CODE_SUCCESS = "00000000";
    public static final String HANDLE_CODE_FAIL = "99999999";
    public static final String HANDLE_CODE_TIMEOUT = "00000001";

    // ---------------------- base info ----------------------

    /**
     * job id
     */
    private final long jobId;

    /**
     * job param
     */
    private final String jobParam;

    // ---------------------- for log ----------------------

    /**
     * job log filename
     */
    private final String jobLogFileName;

    // ---------------------- for shard ----------------------

    /**
     * shard index
     */
    private final int shardIndex;

    /**
     * shard total
     */
    private final int shardTotal;

    // ---------------------- for handle ----------------------

    /**
     * handleCode：The result status of job execution
     * TODO 调度中心调用执行器状态和执行器执行结束的状态：99999999-失败；00000000-成功
     *
     *      99999999 : fail
     *      00000000 : success
     *      00000001 : timeout
     *
     */
    private String handleCode;

    /**
     * handleMsg：The simple log msg of job execution
     */
    private String handleMsg;


    public JobContext(long jobId, String jobParam, String jobLogFileName, int shardIndex, int shardTotal) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;

        this.handleCode = HANDLE_CODE_SUCCESS;  // default success
    }

    public long getJobId() {
        return jobId;
    }

    public String getJobParam() {
        return jobParam;
    }

    public String getJobLogFileName() {
        return jobLogFileName;
    }

    public int getShardIndex() {
        return shardIndex;
    }

    public int getShardTotal() {
        return shardTotal;
    }

    public String getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(String handleCode) {
        this.handleCode = handleCode;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    // ---------------------- tool ----------------------

    private static InheritableThreadLocal<JobContext> contextHolder = new InheritableThreadLocal<JobContext>(); // support for child thread of job handler)

    public static void setJobContext(JobContext jobContext){
        contextHolder.set(jobContext);
    }

    public static JobContext getJobContext(){
        return contextHolder.get();
    }

}