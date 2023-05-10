package com.alita.framework.id.sonwflake;

import com.alita.framework.id.snowflake.SnowFlakeId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * SnowFlakeIdTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2022-11-20 00:25
 **/
@DisplayName("SnowFlakeId")
public class SnowFlakeIdTest {

    @Test
    @DisplayName("generateIdTiny方法")
    public void generateIdTiny() {
        SnowFlakeId mock = SnowFlakeId.getInstance();
        System.out.println(mock.generateIdTiny());
        Assertions.assertNotNull(mock.generateIdTiny());
        Assertions.assertNotEquals(mock.generateIdTiny(), 0L);
    }

    @Test
    @DisplayName("generateIdMini方法")
    public void generateIdMini() {
        SnowFlakeId mock = SnowFlakeId.getInstance();
        System.out.println(mock.generateIdMini());
        Assertions.assertNotNull(mock.generateIdMini());
        Assertions.assertNotEquals(mock.generateIdMini(), 0L);
    }

    @Test
    @DisplayName("generateId48方法,生成14位id")
    public void generateId48() {
        SnowFlakeId mock = SnowFlakeId.getInstance();
        System.out.println(mock.generateId48());
        Assertions.assertNotNull(mock.generateId48());
        Assertions.assertNotEquals(mock.generateId48(), 0L);
    }

    @Test
    @DisplayName("generateId64方法,生成19位id")
    public void generateId64() {
        SnowFlakeId mock = SnowFlakeId.getInstance();
        System.out.println(mock.generateId64());
        Assertions.assertNotNull(mock.generateId64());
        Assertions.assertNotEquals(mock.generateId64(), 0L);
    }

    @Test
    @DisplayName("generateId128方法,生成'负数'19位id")
    public void generateId128() {
        SnowFlakeId mock = SnowFlakeId.getInstance();
        System.out.println(mock.generateId128());
        Assertions.assertNotNull(mock.generateId128());
        Assertions.assertNotEquals(mock.generateId128(), 0L);
    }

}
