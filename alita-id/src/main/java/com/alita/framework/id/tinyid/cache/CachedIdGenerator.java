package com.alita.framework.id.tinyid.cache;

import com.alita.framework.id.tinyid.IdGenerator;
import com.alita.framework.id.tinyid.SegmentIdService;
import com.alita.framework.id.tinyid.TinyIdSysException;
import com.alita.framework.id.tinyid.domain.Result;
import com.alita.framework.id.tinyid.domain.ResultCode;
import com.alita.framework.id.tinyid.domain.SegmentId;
import com.alita.framework.id.tinyid.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * id 生成器
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 13:12
 **/
public class CachedIdGenerator implements IdGenerator {

    public static final Logger logger = LoggerFactory.getLogger(CachedIdGenerator.class);

    protected String bizType;
    protected SegmentIdService segmentIdService;
    protected volatile SegmentId current;
    protected volatile SegmentId next;
    private volatile boolean isLoadingNext;
    private Object lock = new Object();
    private static final ThreadFactory threadFactory = new NamedThreadFactory("tinyid-generator", false);
    private ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);

    public CachedIdGenerator(String bizType, SegmentIdService segmentIdService) {
        this.bizType = bizType;
        this.segmentIdService = segmentIdService;
        loadCurrent();
    }

    public synchronized void loadCurrent() {
        if (current == null || !current.useful()) {
            if (next == null) {
                SegmentId segmentId = querySegmentId();
                this.current = segmentId;
            } else {
                current = next;
                next = null;
            }
        }
    }

    @Override
    public Long nextId() {
        while (true) {
            if (current == null) {
                loadCurrent();
                continue;
            }
            Result result = current.nextId();
            if (result.getCode() == ResultCode.OVER) {
                loadCurrent();
            } else {
                if (result.getCode() == ResultCode.LOADING) {
                    loadNext();
                }
                return result.getId();
            }
        }
    }

    @Override
    public List<Long> nextId(Long batchSize) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            Long id = nextId();
            ids.add(id);
        }
        return ids;
    }


    private SegmentId querySegmentId() {
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            if (segmentId != null) {
                return segmentId;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TinyIdSysException("error query segmentId: " + e.getMessage());
        }
        return null;
    }

    public void loadNext() {
        if (next == null && !isLoadingNext) {
            Runnable runnable = () -> {
                try {
                    // 无论获取下个segmentId成功与否，都要将isLoadingNext赋值为false
                    next = querySegmentId();
                } finally {
                    isLoadingNext = false;
                }
            };

            synchronized (lock) {
                if (next == null && !isLoadingNext) {
                    isLoadingNext = true;
                    executorService.submit(runnable);
                }
            }
        }
    }


}
