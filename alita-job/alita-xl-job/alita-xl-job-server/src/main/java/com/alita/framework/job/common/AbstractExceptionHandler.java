package com.alita.framework.job.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 统一异常处理
 */
@RestControllerAdvice
public class AbstractExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public Response illegalArgument(IllegalArgumentException ex) {
        logger.error(ex.getMessage(), ex);

        return Response.error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response exceptionHandler(Exception ex) {
        logger.error(ex.getMessage(), ex);

        return Response.error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage());
    }

}
