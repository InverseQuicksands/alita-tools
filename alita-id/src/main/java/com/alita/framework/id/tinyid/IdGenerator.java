package com.alita.framework.id.tinyid;

import java.util.List;

/**
 * IdGenerator
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 13:10
 **/
public interface IdGenerator {

    /**
     * 获取下一个 id.
     *
     * @return id
     */
    Long nextId();

    /**
     * 获取下一批 id.
     *
     * @param batchSize 业务类型
     * @return id
     */
    List<Long> nextId(Long batchSize);
}
