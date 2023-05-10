package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.biz.model.TriggerParam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 路由策略：最不经常使用.
 *
 * <p>
 * 使用频率最低的机器优先被选举.
 *
 * <p>
 * 使用 {@code ConcurrentMap<String, HashMap<String, Integer>> jobLfuMap} 记录每个任务 ID 在路由地址下的调用次数，
 * 选取调用次数最小的路由地址.记录的缓存每24小时清空一次.<br>
 * 原理是维护了一个以任务 id 为单位的地址计数器，记录每个任务 id 在路由地址下的调用次数，当第一次进入时，
 * 不知道谁使用最少，以随机的形式先给各个地址初始化一个数，最大的计数器值不超过地址总量。
 *
 * <p>
 * 注意：<br>
 * 依照实现逻辑，如果运行中修改配置，给执行器添加了个路由地址，短时间内调度会全部涌入新地址中。高频任务调度的情况下需要注意。
 *
 * <p>
 * 单个JOB对应的每个执行器，使用频率最低的优先被选举 <br>
 * a：LFU(Least Frequently Used)：最不经常使用，频率/次数 <br>
 * b：LRU(Least Recently Used)：最近最久未使用，时间 <br>
 */
public class ExecutorRouteLFU extends ExecutorRouter {

    /**
     * key：jobId，value：{key-路由地址，value-计数器}
     */
    private static ConcurrentMap<String, HashMap<String, Integer>> jobLfuMap = new ConcurrentHashMap<String, HashMap<String, Integer>>();
    private static long CACHE_VALID_TIME = 0;


    public String route(String jobId, List<String> addressList) {

        // cache clear
        // 超过一天后重置缓存
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLfuMap.clear();
            // 缓存周期为一天
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;
        }

        // lfu item init
        HashMap<String, Integer> lfuItemMap = jobLfuMap.get(jobId);     // Key排序可以用TreeMap+构造入参Compare；Value排序暂时只能通过ArrayList；
        if (lfuItemMap == null) {
            lfuItemMap = new HashMap<String, Integer>();
            jobLfuMap.putIfAbsent(jobId, lfuItemMap);   // 避免重复覆盖
        }

        // put new
        for (String address: addressList) {
            if (!lfuItemMap.containsKey(address) || lfuItemMap.get(address) > 1000000) {
                lfuItemMap.put(address, new Random().nextInt(addressList.size()));  // 初始化时主动Random一次，缓解首次压力
            }
        }
        // remove old
        List<String> delKeys = new ArrayList<>();
        // 获取 lfuItemMap 中所有的执行器地址，遍历它判断传入的 addressList 是否包含，如果不包含则删除多余的 address.
        // 一般只有 lfuItemMap 中的数量大于 addressList 列表的数量时才会有效.
        for (String existKey: lfuItemMap.keySet()) {
            if (!addressList.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (delKeys.size() > 0) {
            for (String delKey: delKeys) {
                lfuItemMap.remove(delKey);
            }
        }

        // load least userd count address
        // 将 lfuItemMap：{key-路由地址，value-计数器} 放入 list 中进行排序，次数由小到排序.
        List<Map.Entry<String, Integer>> lfuItemList = new ArrayList<Map.Entry<String, Integer>>(lfuItemMap.entrySet());
        Collections.sort(lfuItemList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        // 取出次数最少的 address，并且次数+1.
        Map.Entry<String, Integer> addressItem = lfuItemList.get(0);
        addressItem.setValue(addressItem.getValue() + 1);

        return addressItem.getKey();
    }

    /**
     * 最不经常使用路由策略.
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
