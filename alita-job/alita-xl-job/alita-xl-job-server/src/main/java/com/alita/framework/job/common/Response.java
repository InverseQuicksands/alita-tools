package com.alita.framework.job.common;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private static final long serialVersionUID = 3682681750583747293L;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应信息
     */
    public String message;

    /**
     * 返回数据
     */
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
