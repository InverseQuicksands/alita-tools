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

package com.alita.framework.platform.autoconfigure.advice.response;

import java.io.Serializable;

/**
 * 响应信息
 *
 * @author Zhang Liang
 * @date 2021/2/9
 * @since 1.0
 */

public class Response<T> implements Serializable {

    private static final long serialVersionUID = 8418439919243662553L;

    /** 错误码，给出明确错误码，更好的应对业务异常；请求成功该值可为空 */
    private String code = "200";

    /** 错误消息，与错误码相对应，更具体的描述异常信息 */
    private String message = "";

    /** 返回结果，通常是 Bean 对象对应的 JSON 数据, 通常为了应对不同返回值类型，将其声明为泛型类型 */
    private T data;

    public static <T> Response<T> success() {
        return success(ResponseStatus.SUCCESS.getCode());
    }

    public static <T> Response<T> success(String code) {
        return success(code, null);
    }

    public static <T> Response<T> success(String code, String message) {
        return success(code, message, null);
    }

    public static <T> Response<T> success(T data) {
        return success(ResponseStatus.SUCCESS.getCode(), null, data);
    }


    public static <T> Response<T> success(String code, String message, T data) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(String code, String msg) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(msg);
        return response;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }


}
