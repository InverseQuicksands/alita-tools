package com.alita.framework.platform.autoconfigure.advice.response;

/**
 * 响应结果枚举
 */
public enum ResponseStatus {

    SUCCESS ("00000000", "请求成功"),

    FAILED ("400", "请求失败"),

    NOT_FOUND ("404", "接口不存在"),

    SIGNATURE_NOT_MATCH("401","没有权限"),

    INTERNAL_SERVER_ERROR("99999999", "服务器内部错误");


    private String code ;

    private String message ;

    ResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
