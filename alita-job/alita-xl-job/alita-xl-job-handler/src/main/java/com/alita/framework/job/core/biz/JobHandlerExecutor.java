package com.alita.framework.job.core.biz;

import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.biz.model.LogParam;
import com.alita.framework.job.core.biz.model.LogResult;
import com.alita.framework.job.core.biz.model.TriggerParam;

import java.io.IOException;

/**
 * 抽象 job 执行器
 */
public interface JobHandlerExecutor {

    /**
     * 是否空闲
     *
     * @param executorParam 执行参数
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    String idleBeat(ExecutorParam executorParam) throws IOException;

    /**
     * 心跳检测
     *
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    String beat() throws IOException;



    /**
     * 运行任务
     *
     * @param triggerParam 执行参数
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    String run(TriggerParam triggerParam) throws IOException;

    /**
     * 关闭任务
     *
     * @param killParam 执行参数
     * @return 响应结果：成功-{"code": "00000000"}，失败-{"code": "99999999"}
     */
    String kill(ExecutorParam killParam) throws IOException;

    /**
     * log
     *
     * @param logParam 执行参数
     * @return LogResult
     */
    LogResult log(LogParam logParam) throws IOException;

}
