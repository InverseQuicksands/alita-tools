package com.alita.framework.id.tinyid.client;

import com.alita.framework.id.tinyid.TinyIdSysException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@DisplayName("IdGeneratorFactoryClient")
public class IdGeneratorFactoryClientTest {

    @Test
    @DisplayName("nextId方法")
    public void nextId() {
        Tinyid tinyid = Tinyid.getInstance("127.0.0.1:8100");
//        List<Long> ids = tinyid.nextId("test", 10L);
//        Assertions.assertTrue(ids.size() == 10);
        TinyIdSysException exception = Assertions.assertThrows(
                TinyIdSysException.class, () -> tinyid.nextId(Mockito.anyString(), Mockito.anyLong()));
        Assertions.assertTrue(exception.getMessage().contains("tinyId client getNextSegmentId error"));
    }



}
