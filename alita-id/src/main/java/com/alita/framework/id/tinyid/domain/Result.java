package com.alita.framework.id.tinyid.domain;

/**
 * Result
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 13:01
 **/
public class Result {

    private int code;

    private long id;

    public Result(int code, long id) {
        this.code = code;
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", id=" + id +
                '}';
    }
}
