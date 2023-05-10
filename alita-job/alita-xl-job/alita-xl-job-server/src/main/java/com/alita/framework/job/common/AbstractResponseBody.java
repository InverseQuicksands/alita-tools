package com.alita.framework.job.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * 公共响应类
 */
@RestControllerAdvice
public class AbstractResponseBody implements ResponseBodyAdvice {

    private static final Logger logger = LoggerFactory.getLogger(AbstractResponseBody.class);

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        boolean stringAssignable = StringHttpMessageConverter.class.isAssignableFrom(converterType);
        boolean responseAssignableFrom = Response.class.isAssignableFrom(returnType.getMethod().getReturnType());

        if (stringAssignable || responseAssignableFrom) {
            return false;
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        return Response.success(body);
    }
}
