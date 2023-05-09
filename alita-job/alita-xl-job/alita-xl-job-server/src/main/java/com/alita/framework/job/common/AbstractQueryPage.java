package com.alita.framework.job.common;

public abstract class AbstractQueryPage {

    /**
     * 当前页
     */
    private int currentPage = 1;

    /**
     * 分页数
     */
    private int pageSize = 10;

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

    public int getOffset() {
        if (currentPage >= 1) {
            return (currentPage - 1) * pageSize;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "AbstractSelectPage{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}
