package com.alita.framework.id.tinyid.cache;

import com.alita.framework.id.tinyid.domain.SegmentId;
import com.alita.framework.id.tinyid.server.service.DbSegmentIdServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@DisplayName("CachedIdGenerator")
public class CachedIdGeneratorTest {

    @Test
    @DisplayName("nextId方法")
    public void nextId() {
        SegmentId segmentId = new SegmentId();
        segmentId.setCurrentId(new AtomicLong(10000L));
        segmentId.setMaxId(20001L);
        segmentId.setRemainder(0);
        segmentId.setDelta(1);
        segmentId.setLoadingId(12000L);

        DbSegmentIdServiceImpl dbSegmentIdService = Mockito.mock(DbSegmentIdServiceImpl.class);
        Mockito.when(dbSegmentIdService.getNextSegmentId("test")).thenReturn(segmentId);
        CachedIdGenerator idGenerator = new CachedIdGenerator("test", dbSegmentIdService);

        List<Long> ids = idGenerator.nextId(10L);
        Assertions.assertTrue(ids.size() == 10);
        System.out.println(ids);
    }



}
