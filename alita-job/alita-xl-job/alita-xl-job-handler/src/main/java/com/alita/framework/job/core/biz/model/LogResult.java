package com.alita.framework.job.core.biz.model;

import java.io.Serializable;

/**
 * LogResult
 *
 * @date 2022-11-21 22:08
 */
public class LogResult implements Serializable {

    private static final long serialVersionUID = 7578261586573481916L;

    private int fromLineNum;
    private int toLineNum;
    private String logContent;
    private boolean isEnd;

    public LogResult() {

    }

    public LogResult(int fromLineNum, int toLineNum, String logContent, boolean isEnd) {
        this.fromLineNum = fromLineNum;
        this.toLineNum = toLineNum;
        this.logContent = logContent;
        this.isEnd = isEnd;
    }

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

    public int getToLineNum() {
        return toLineNum;
    }

    public void setToLineNum(int toLineNum) {
        this.toLineNum = toLineNum;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    @Override
    public String toString() {
        return "LogResult{" +
                "fromLineNum=" + fromLineNum +
                ", toLineNum=" + toLineNum +
                ", logContent='" + logContent + '\'' +
                ", isEnd=" + isEnd +
                '}';
    }
}
