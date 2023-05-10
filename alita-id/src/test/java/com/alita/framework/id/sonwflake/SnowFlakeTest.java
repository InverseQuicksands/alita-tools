package com.alita.framework.id.sonwflake;

import com.alita.framework.id.snowflake.SnowFlake;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * SnowFlakeTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2022-11-19 23:40
 **/
@DisplayName("SnowFlake")
public class SnowFlakeTest {


    @Test
    @DisplayName("nextId方法")
    public void nextId() {
        SnowFlake snowFlake = Mockito.spy(SnowFlake.class);
        Assertions.assertNotNull(snowFlake.nextId());
        Assertions.assertTrue(snowFlake.nextId() > 0);
    }




}
