package com.alita.framework.job.common;

import java.io.Serializable;
import java.util.List;

public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 6419851166495192908L;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 分页数
     */
    private int pageSize;

    /**
     * 总条数
     */
    private int totalNum;

    /**
     * 数据
     */
    private List<T> list;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalNum=" + totalNum +
                ", list=" + list +
                '}';
    }
}
