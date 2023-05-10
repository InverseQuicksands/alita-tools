package com.alita.framework.job.core.executor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.StringJoiner;

/**
 * JobFileAppenderTest
 *
 * @date 2022-11-21 22:56
 */
@DisplayName("JobFileAppender")
public class JobFileAppenderTest {


    @Test
    public void test() {
        String property = System.getProperty("line.separator");
        StringJoiner joiner = new StringJoiner(property);
        for (int i=0; i<3; i++) {
            joiner.add(i+"");
        }

        System.out.println(joiner);
    }



}
