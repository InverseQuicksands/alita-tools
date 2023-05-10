package com.alita.framework.job.common;

import java.util.List;

/**
 * 业务抽象类
 */
public abstract class AbstractServiceImpl<T> {


    /**
     * 返回分页数据.
     * @param selectPage 分页对象
     * @param list 数据
     * @param count 统计
     * @return PageInfo
     */
    public PageInfo<T> pageInfo(AbstractQueryPage selectPage, List<T> list, int count) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setCurrentPage(selectPage.getCurrentPage());
        pageInfo.setPageSize(selectPage.getPageSize());
        pageInfo.setTotalNum(count);
        pageInfo.setList(list);

        return pageInfo;
    }

    /**
     * 生成主键id.
     *
     * @return long
     */
    public long increment() {
        return SnowFlakeId.getInstance().generateId48();
    }

}
