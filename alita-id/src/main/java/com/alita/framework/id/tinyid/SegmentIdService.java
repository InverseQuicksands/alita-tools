package com.alita.framework.id.tinyid;

import com.alita.framework.id.tinyid.domain.SegmentId;

/**
 * SegmentIdService
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2022-11-17 13:19
 **/
public interface SegmentIdService {

    /**
     * 根据 bizType 获取下一个 SegmentId 对象.
     *
     * @param bizType 业务类型
     * @return SegmentId
     */
    SegmentId getNextSegmentId(String bizType);
}
