package com.alita.framework.job.core.handler.impl;

import com.alita.framework.job.core.context.JobContext;
import com.alita.framework.job.core.context.JobHelper;
import com.alita.framework.job.core.executor.JobFileAppender;
import com.alita.framework.job.core.glue.GlueTypeEnum;
import com.alita.framework.job.core.handler.IJobHandler;
import com.alita.framework.job.utils.ScriptUtil;

import java.io.File;

/**
 */
public class ScriptJobHandler implements IJobHandler {

    private String jobId;
    private long glueUpdatetime;
    private String gluesource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(String jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType){
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;
        this.glueType = glueType;

        // clean old script file
        File glueSrcPath = new File(JobFileAppender.getGlueSrcPath());
        if (glueSrcPath.exists()) {
            File[] glueSrcFileList = glueSrcPath.listFiles();
            if (glueSrcFileList!=null && glueSrcFileList.length>0) {
                for (File glueSrcFileItem : glueSrcFileList) {
                    if (glueSrcFileItem.getName().startsWith(String.valueOf(jobId)+"_")) {
                        glueSrcFileItem.delete();
                    }
                }
            }
        }

    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public void execute() throws Exception {

        if (!glueType.isScript()) {
            JobHelper.handleFail("glueType["+ glueType +"] invalid.");
            return;
        }

        // cmd
        String cmd = glueType.getCmd();

        // make script file
        String scriptFileName = JobFileAppender.getGlueSrcPath()
                .concat(File.separator)
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdatetime))
                .concat(glueType.getSuffix());
        File scriptFile = new File(scriptFileName);
        if (!scriptFile.exists()) {
            ScriptUtil.markScriptFile(scriptFileName, gluesource);
        }

        // log file
        String logFileName = JobContext.getJobContext().getJobLogFileName();

        // script params：0=param、1=分片序号、2=分片总数
        String[] scriptParams = new String[3];
        scriptParams[0] = JobHelper.getJobParam();
        scriptParams[1] = String.valueOf(JobContext.getJobContext().getShardIndex());
        scriptParams[2] = String.valueOf(JobContext.getJobContext().getShardTotal());

        // invoke
        JobHelper.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);

        if (exitValue == 0) {
            JobHelper.handleSuccess();
            return;
        } else {
            JobHelper.handleFail("script exit value("+exitValue+") is failed");
            return ;
        }

    }

    /**
     * init handler, invoked when JobThread init
     */
    @Override
    public void init() throws Exception {

    }

    /**
     * destroy handler, invoked when JobThread destroy
     */
    @Override
    public void destroy() throws Exception {

    }

}
