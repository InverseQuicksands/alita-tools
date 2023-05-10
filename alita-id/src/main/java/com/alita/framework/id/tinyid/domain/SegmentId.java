package com.alita.framework.id.tinyid.domain;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SegmentId
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 13:03
 **/
public class SegmentId {

    /**
     * 当前最大的可用id
     */
    private long maxId;
    private long loadingId;
    private AtomicLong currentId;

    /**
     * id每次的增量
     */
    private int delta;

    /**
     * 余数
     */
    private int remainder;

    private AtomicBoolean isInit = new AtomicBoolean();

    /**
     * 为 currentId 设置初始符合条件的值.
     *
     * <p>
     * 这个方法主要为了1,4,7,10...这种序列准备的. <br>
     * 设置好初始值之后，会以delta的方式递增，保证无论开始id是多少都能生成正确的序列. <br>
     * 如当前是号段是(1000,2000]，delta=3， remainder=0，则经过这个方法后，currentId会先递增到1002，
     * 之后每次增加 delta，因为 currentId 会先递增，所以会浪费一个id，所以做了一次减delta的操作。
     * 实际 currentId 会从999开始增，第一个 id 还是1002
     */
    public void initCurrentId() {
        if (isInit.get()) {
            return;
        }
        synchronized (this) {
            if (isInit.get()) {
                return;
            }
            long id = currentId.get();
            // 获取的 id 模增量等于余量，则 isInit 为 true
            if (id % delta == remainder) {
                isInit.set(true);
                return;
            }
            for (int i = 0; i <= delta; i++) {
                id = currentId.incrementAndGet();
                if (id % delta == remainder) {
                    // 避免浪费 减掉系统自己占用的一个id
                    currentId.addAndGet(0 - delta);
                    isInit.set(true);
                    return;
                }
            }
        }
    }

    public Result nextId() {
        initCurrentId();
        long id = currentId.addAndGet(delta);
        if (id > maxId) {
            return new Result(ResultCode.OVER, id);
        }
        if (id >= loadingId) {
            return new Result(ResultCode.LOADING, id);
        }
        return new Result(ResultCode.NORMAL, id);
    }

    public boolean useful() {
        return currentId.get() <= maxId;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public long getLoadingId() {
        return loadingId;
    }

    public void setLoadingId(long loadingId) {
        this.loadingId = loadingId;
    }

    public AtomicLong getCurrentId() {
        return currentId;
    }

    public void setCurrentId(AtomicLong currentId) {
        this.currentId = currentId;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getRemainder() {
        return remainder;
    }

    public void setRemainder(int remainder) {
        this.remainder = remainder;
    }

    @Override
    public String toString() {
        return "SegmentId{" +
                "maxId=" + maxId +
                ", loadingId=" + loadingId +
                ", currentId=" + currentId +
                ", delta=" + delta +
                ", remainder=" + remainder +
                '}';
    }
}
