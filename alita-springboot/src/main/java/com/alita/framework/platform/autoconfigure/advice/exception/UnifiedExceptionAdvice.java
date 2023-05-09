/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.alita.framework.platform.autoconfigure.advice.exception;

import com.alita.framework.platform.autoconfigure.advice.response.Response;
import com.alita.framework.platform.autoconfigure.file.MultipartProperties;
import com.alita.framework.platform.exception.BusinessException;
import com.alita.framework.platform.exception.TimeoutException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

/**
 * 全局异常处理
 * <p>返回统一异常响应
 *
 * @author Zhang Liang
 * @date 2021/2/9
 * @since 1.0
 */
@RestControllerAdvice
@EnableConfigurationProperties(value = {MultipartProperties.class})
public class UnifiedExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedExceptionAdvice.class);

    @Autowired
    private MultipartProperties multipartProperties;


    /**
     * 业务异常处理
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response businessException(BusinessException ex, HttpServletRequest request) {
        logger.error("url:{} 业务异常,原因:{}", request.getRequestURL(), ex.getMessage());
        return Response.error("QD0001", ex.getMessage());
    }


    /**
     * 参数异常处理
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response invalidArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        logger.error("url:{} 参数异常,原因:{}", request.getRequestURL(), ex.getMessage());
        return Response.error("QD0001", ex.getMessage());
    }


    /**
     * 数据库异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response dataAccessException(DataAccessException ex, HttpServletRequest request) {
        logger.error("url:{} 数据库异常,原因:{}", request.getRequestURL(), ex.getMessage());
        return Response.error("QD0001", ex.getMessage());
    }

    /**
     * 空指针异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response nullPointerException(NullPointerException ex, HttpServletRequest request) {
        logger.error("url:{} 空指针异常,原因:{}", request.getRequestURL(), ex.getMessage());
        return Response.error("QD0001", ex.getMessage());
    }


    /**
     * 文件传输异常
     *
     * @param multipartException
     * @return
     */
    @ExceptionHandler(value = MultipartException.class)
    public Response dealMulipartException(MultipartException multipartException) {
        Throwable cause = multipartException.getCause();
        Response restResponse = new Response<>();
        restResponse.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        if (cause instanceof FileSizeLimitExceededException) {
            restResponse.setMessage("单个上传文件大小不能超过" + multipartProperties.getMaxFileSize());
        } else if (cause instanceof SizeLimitExceededException) {
            restResponse.setMessage("上传文件总大小不能超过" + multipartProperties.getMaxRequestSize());
        }

        return restResponse;
    }

    /**
     * 连接超时异常处理
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = TimeoutException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response timeoutException(TimeoutException ex, HttpServletRequest request) {
        logger.error("url:{} 连接超时,原因:{}", request.getRequestURL(), ex.getMessage());
        return Response.error("QD0001", ex.getMessage());
    }




//    @ExceptionHandler(value = {Exception.class})
//    public ModelAndView exception(Exception ex, HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        ModelAndView modelAndView = new ModelAndView();
//        if (uri.contains("enterprise")) {
//            MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
//            modelAndView.setView(jsonView);
//            modelAndView.addObject(ex.getMessage());
//            return modelAndView;
//        } else {
//            RedirectView redirectView = new RedirectView("/static/error/500.jsp");
//            redirectView.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//            modelAndView.setView(redirectView);
//            return modelAndView;
//        }
//    }

}
