package com.alita.framework.job.core.route.strategy;

import com.alita.framework.job.core.route.ExecutorRouter;
import com.alita.framework.job.core.biz.model.TriggerParam;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 路由策略：一致性Hash.
 * <p>
 * 通过一致性Hash，每个任务按照Hash算法固定选择某一台机器，且所有任务均匀散列在不同机器上，具体哪台机器根据hash值的范围选取。
 * 同时通过循环，扩充hash的集合大小，以保证分组下机器分配job足够平均。
 *
 * <p>
 * 分组下机器地址相同，不同JOB均匀散列在不同机器上，保证分组下机器分配JOB平均；且每个JOB固定调度其中一台机器；<br>
 * a：virtual node：解决不均衡问题.<br>
 * b：hash method replace hashCode：String的hashCode可能重复，需要进一步扩大hashCode的取值范围.<br>
 */
public class ExecutorRouteConsistentHash extends ExecutorRouter {

    private static int VIRTUAL_NODE_NUM = 100;

    /**
     * get hash code on 2^32 ring (md5散列的方式计算hash值).
     *
     * @param key
     * @return hash 值
     */
    private static long hash(String key) {
        // md5 byte
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try {
            keyBytes = key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unknown string :" + key, e);
        }

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }

    public String hashJob(String jobId, List<String> addressList) {

        // ------A1------A2-------A3------
        // -----------J1------------------
        TreeMap<Long, String> addressRing = new TreeMap<Long, String>();
        // 遍历所有执行器地址列表，计算相同地址不同的 hash 值并放入 addressRing 中. key-addressHash，value-address
        for (String address: addressList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long addressHash = hash("SHARD-" + address + "-NODE-" + i);
                addressRing.put(addressHash, address);
            }
        }
        // 单独计算 jobId 的 hash 值.
        long jobHash = hash(jobId);
        // 取出集合中 hash 值大于等于 jobHash 的第一个 hash 值对应的 address.
        SortedMap<Long, String> lastRing = addressRing.tailMap(jobHash);
        if (!lastRing.isEmpty()) {
            return lastRing.get(lastRing.firstKey());
        }
        // 如果 lastRing 为空，则返回 addressRing 中第一个 hash 值的 address 地址.
        return addressRing.firstEntry().getValue();
    }

    /**
     * 一致性Hash路由策略.
     *
     * @param triggerParam 执行参数
     * @param addressList 执行机器列表
     * @return address 地址
     */
    @Override
    public String route(TriggerParam triggerParam, List<String> addressList) {
        return hashJob(triggerParam.getJobId(), addressList);
    }

}
