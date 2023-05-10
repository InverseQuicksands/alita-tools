package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.biz.model.TriggerParam;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 路由策略：轮询.
 *
 * <p>
 * 通过 {@code ConcurrentMap<String, AtomicInteger>} 缓存记录任务ID以及执行数字，以执行数字取模作为路由地址下标，选取地址返回。
 * 每次调用后执行数字 +1，达到路由地址轮询调用的作用。
 *
 * <p>
 * 轮询并非是从第一个开始，而是随机选择开始的位置，每次通过自增后取模来定位到下一个地址，为了防止integer无限增大，
 * 每24小时会清除一次位置信息，重新随机定位。
 */
public class ExecutorRouteRound extends ExecutorRouter {

    // 轮询记录 key：JobId，value：调用次数递增数字(初始值为100以内的随机数, 每次调用递增, 用于取模作为路由地址数组index, 决定当次调用路由)
    private static ConcurrentMap<String, AtomicInteger> routeCountEachJob = new ConcurrentHashMap<>();
    private static long CACHE_VALID_TIME = 0;

    private static int count(String jobId) {
        // cache clear 每24小时清空一次 轮询记录缓存
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountEachJob.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;
        }

        AtomicInteger count = routeCountEachJob.get(jobId);
        if (count == null || count.get() > 1000000) {
            // 初始化时主动Random一次，缓解首次压力
            count = new AtomicInteger(new Random().nextInt(100));
        } else {
            // count++
            count.addAndGet(1);
        }
        routeCountEachJob.put(jobId, count);
        return count.get();
    }

    /**
     * 轮询路由策略.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行机器列表
     * @return address 地址
     */
    @Override
    public String route(TriggerParam triggerParam, List<String> addressList) {
        String address = addressList.get(count(triggerParam.getJobId()) % addressList.size());
        return address;
    }

}
