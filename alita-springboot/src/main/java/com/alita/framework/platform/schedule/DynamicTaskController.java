package com.alita.framework.platform.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class DynamicTaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicTaskController.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;


    /**
     * 在ScheduledFuture中有一个cancel可以停止定时任务。
     */
    private ScheduledFuture<?> future;


    /**
     * 启动任务
     **/
    @GetMapping("/startTask")
    public String startCron() {
        future = threadPoolTaskScheduler.schedule(new SchedulingRunnable(), new CronTrigger("0/5 * * * * *"));


        LOGGER.debug("DynamicTaskController.startCron()");
        try{
            //get()方法用来获取执行结果，这个方法会产生阻塞，会一直等到任务执行完毕才返回；
            //future的get()设定超时时间
            Object o = future.get(3900, TimeUnit.MILLISECONDS);
            LOGGER.debug("==o=>>>  "+o);
            Object o1 = future.get();
            LOGGER.debug("==o1=>>>  "+o1);
        }
        catch ( Exception e){
            System.out.println(e);
        }
        return "startTask";
    }

    /**
     * 启此任务
     **/
    @GetMapping("/stopTask")
    public String stopCron() {
        if (future != null) {
            future.cancel(true);
        }
        LOGGER.debug("DynamicTaskController.stopCron()");
        return "stopTask";
    }

    /**
     * 变更任务间隔，再次启动
     **/
    @GetMapping("/changeCron")
    public String changeCron() {
        stopCron();// 先停止，在开启.
        future = threadPoolTaskScheduler.schedule(new SchedulingRunnable(), new CronTrigger("*/10 * * * * *"));
        LOGGER.debug("DynamicTaskController.changeCron()");
        return "changeCron";
    }

    class SchedulingRunnable implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingRunnable.class);

        @Override
        public void run() {
            LOGGER.debug("现在执行函数的时间");
        }
    }
}
