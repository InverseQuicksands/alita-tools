package com.alita.framework.platform.autoconfigure.advice.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * 全局请求处理.
 * <p>利用 spring mvc {@code RequestMappingHandlerAdapter} 处理类，进行请求参数的处理。如：请求参数的打印.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-04-26 09:35:56
 */
@RestControllerAdvice
public class UnifiedRequestAdvice extends RequestBodyAdviceAdapter {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedRequestAdvice.class);


    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        logger.info("【请求参数】：{}", body.toString());
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}
