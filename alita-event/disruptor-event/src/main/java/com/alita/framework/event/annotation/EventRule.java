package com.alita.framework.event.annotation;

import java.lang.annotation.*;

/**
 * Subscript
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-05-06 10:40:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventRule {

    /**
     * Ant风格的事件分发规则表达式,格式为：/event/tags/keys，如：/aaa/bbb/**.
     *
     * @return 规则表达式
     */
    String rule() default "*";


    String value();

}
