package com.alita.framework.id.snowflake;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Twitter的 Snowflake　JAVA实现方案.
 *
 * <p>
 * Snowflake算法是带有时间戳的全局唯一ID生成算法。它有一套固定的ID格式，如下：
 * 每部分用-分开：
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 *
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0 <br>
 *
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截得到的值），
 * 这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的。
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69 <br>
 *
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId <br>
 *
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号加起来刚好64位，为一个Long型。
 *
 * <p>
 * SnowFlake的优点是：整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高。<br>
 * 经测试，snowflake每秒能够产生26万ID左右，完全满足需要。
 * 64位ID (42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-13 13:47
 */
public class SnowFlake {

    private static final Logger logger = LoggerFactory.getLogger(SnowFlake.class);


    /** 系统开始时间截 */
    private final long startTime = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long MACHINE_BIT = 5;     //机器标识占用的位数
    private final static long DATA_CENTER_BIT = 5; //数据中心占用的位数
    private final static long SEQUENCE_BIT = 12;   //序列号占用的位数


    /**
     * 每一部分的最大值
     */
    // 支持的最大机器id(十进制)，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) -1L 左移 5位 (worker id 所占位数) 即 5位二进制所能获得的最大十进制数 - 31
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    // 支持的最大数据标识id - 31
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
    // 生成序列的掩码(12位所对应的最大整数值)，这里为4095 (0b111111111111=0xfff=4095)
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    // 机器ID 左移位数 - 12 (即末 sequence 所占用的位数)
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    // 数据标识id 左移位数 - 17(12+5)
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    // 时间截向左移位数 - 22(5+5+12)
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;
    
    /** ===============================Works's Parameter=============================*/

    private long machineId; // 机器标识
    private long dataCenterId; // 数据中心
    private long sequence = 0L; // 毫秒内序列(0~4095)
    private long lastTimestamp = -1L; // 上一次时间戳

    /** ===============================Constructors==================================*/

    public SnowFlake() {
        long datacenterId = getDatacenterId(MAX_DATA_CENTER_NUM);
        long wordId = getMaxWorkerId(datacenterId, MAX_MACHINE_NUM);

        this.machineId = wordId;
        this.dataCenterId = datacenterId;
    }



    /**
     * 构造函数.
     *
     * <p>
     * 根据指定的数据中心ID和机器标志ID生成指定的序列号
     *
     * @param machineId    机器标志ID
     * @param dataCenterId 数据中心ID
     */
    public SnowFlake(long machineId, long dataCenterId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", MAX_MACHINE_NUM));
        }
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("DataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_NUM));
        }
        this.machineId = machineId;
        this.dataCenterId = dataCenterId;
    }


    /** ================================Methods=======================================*/

    /** 线程安全的获得下一个 ID 的方法 */
    public synchronized long nextId() {
        long timestamp = currentTime();
        // 如果当前时间小于上一次ID生成的时间戳: 说明系统时钟回退过 - 这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大，即：序列 > 4095
            if (sequence == 0L) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = blockTillNextMillis();
            }
        } else {
            //时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return (timestamp - startTime) << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }


    /** 阻塞到下一个毫秒 即 直到获得新的时间戳 */
    private long blockTillNextMillis() {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }


    /** 获得以毫秒为单位的当前时间 */
    private long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 机器标志ID
     */
    protected static long getDatacenterId(long MAX_DATA_CENTER_NUM) {
        long macAddr = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                macAddr = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                macAddr = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                macAddr = macAddr % (MAX_DATA_CENTER_NUM + 1);
            }
        } catch (Exception ex) {
            macAddr = System.currentTimeMillis();
            logger.error("######【获取数据中心ID异常】######", ex);
        }
        return macAddr;
    }


    /**
     * 机器标识
     **/
    protected static long getMaxWorkerId(long datacenterId, long MAX_MACHINE_NUM) {
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /** GET jvmPid */
            mpid.append(name.split("@")[0]);
        }
        /** MAC + PID 的 hashcode 获取16个低位 */
        return (mpid.toString().hashCode() & 0xffff) % (MAX_MACHINE_NUM + 1);
    }

}
