package com.alita.framework.job.core.handler;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * JobHandler
 *
 * @date 2022-12-03 00:29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface JobHandler {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value();
}
