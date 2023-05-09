package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.biz.model.TriggerParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 路由策略：最近最久未使用.
 *
 * <p>
 * 最久未使用的机器优先被选举.
 *
 * <p>
 * 维护了一个以任务id为单位的map，kv都是地址.<br>
 *
 * 实现原理是利用了LinkedHashMap存储排序的特性：<br>
 * {@code accessOrder=true} 时访问顺序排序（get/put时排序），
 * {@code accessOrder=false} 时插入顺序排序.<br>
 *
 * 记录的缓存每24小时清空一次.对于那种一天及以上才执行一次的任务，在地址配置不变的情况下，会一直调度第一个路由地址.
 *
 * <p>
 * 单个JOB对应的每个执行器，使用频率最低的优先被选举 <br>
 * a：LFU(Least Frequently Used)：最不经常使用，频率/次数 <br>
 * b：LRU(Least Recently Used)：最近最久未使用，时间 <br>
 */
public class ExecutorRouteLRU extends ExecutorRouter {

    private static ConcurrentMap<String, LinkedHashMap<String, String>> jobLRUMap = new ConcurrentHashMap<String, LinkedHashMap<String, String>>();
    private static long CACHE_VALID_TIME = 0;


    public String route(String jobId, List<String> addressList) {
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLRUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;
        }

        // init lru
        LinkedHashMap<String, String> lruItem = jobLRUMap.get(jobId);
        if (lruItem == null) {
            /**
             * LinkedHashMap
             * a、accessOrder：true=访问顺序排序（get/put时排序）；false=插入顺序排期；
             * b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */
            lruItem = new LinkedHashMap<String, String>(16, 0.75f, true);
            jobLRUMap.putIfAbsent(jobId, lruItem);
        }

        // put new
        for (String address: addressList) {
            if (!lruItem.containsKey(address)) {
                lruItem.put(address, address);
            }
        }
        // remove old
        List<String> delKeys = new ArrayList<>();
        for (String existKey: lruItem.keySet()) {
            if (!addressList.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (delKeys.size() > 0) {
            for (String delKey: delKeys) {
                lruItem.remove(delKey);
            }
        }

        // load
        String eldestKey = lruItem.entrySet().iterator().next().getKey();
        String eldestValue = lruItem.get(eldestKey);
        return eldestValue;
    }

    /**
     * 最近最久未使用路由策略.
     *
     * <p>
     * 实现原理与 {@link ExecutorRouteLFU} 相似.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行机器列表
     * @return address 地址
     */
    @Override
    public String route(TriggerParam triggerParam, List<String> addressList) {
        String address = route(triggerParam.getJobId(), addressList);
        return address;
    }

}
