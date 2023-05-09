package com.alita.framework.id.tinyid.server.service;

import com.alita.framework.id.tinyid.SegmentIdService;
import com.alita.framework.id.tinyid.TinyIdSysException;
import com.alita.framework.id.tinyid.domain.SegmentId;
import com.alita.framework.id.tinyid.server.Constants;
import com.alita.framework.id.tinyid.server.model.TinyIdInfo;
import com.alita.framework.jdbc.support.JdbcTemplateSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class DbSegmentIdServiceImpl implements SegmentIdService {

    private static final Logger logger = LoggerFactory.getLogger(DbSegmentIdServiceImpl.class);

    @Autowired
    private JdbcTemplateSupport jdbcTemplate;

    /**
     * Transactional标记保证query和update使用的是同一连接
     * 事务隔离级别应该为READ_COMMITTED,Spring默认是DEFAULT(取决于底层使用的数据库，mysql的默认隔离级别为REPEATABLE_READ)
     * <p>
     * 如果是REPEATABLE_READ，那么在本次事务中循环调用tinyIdInfoDAO.queryByBizType(bizType)获取的结果是没有变化的，也就是查询不到别的事务提交的内容
     * 所以多次调用tinyIdInfoDAO.updateMaxId也就不会成功
     *
     * @param bizType
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SegmentId getNextSegmentId(String bizType) {
        // 获取nextTinyId的时候，有可能存在version冲突，需要重试
        for (int i = 0; i < Constants.RETRY; i++) {

            String sql = "select id, biz_type, begin_id, max_id," +
                    " step, delta, remainder, create_time, update_time, version" +
                    " from tiny_id_info where biz_type =:bizType";
            Map<String, Object> map = new HashMap<>(1);
            map.put("bizType", bizType);

            TinyIdInfo tinyIdInfo = jdbcTemplate.queryForObject(sql, map, new TinyIdInfoRowMapper());
            if (tinyIdInfo == null) {
                throw new TinyIdSysException("can not find biztype:" + bizType);
            }
            Long newMaxId = tinyIdInfo.getMaxId() + tinyIdInfo.getStep();
            Long oldMaxId = tinyIdInfo.getMaxId();

            String updateSql = "update tiny_id_info set max_id= :newMaxId," +
                    " update_time=now(), version=version+1" +
                    " where id=:id and max_id=:oldMaxId and version=:version and biz_type=:biz_type";
            Map<String, Object> updateMap = new HashMap<>(5);
            updateMap.put("id", tinyIdInfo.getId());
            updateMap.put("newMaxId", newMaxId);
            updateMap.put("oldMaxId", oldMaxId);
            updateMap.put("version", tinyIdInfo.getVersion());
            updateMap.put("biz_type", tinyIdInfo.getBizType());

            int row = jdbcTemplate.store(updateSql, updateMap);
            if (row == 1) {
                tinyIdInfo.setMaxId(newMaxId);
                SegmentId segmentId = convert(tinyIdInfo);
                logger.info("getNextSegmentId success tinyIdInfo:{} current:{}", tinyIdInfo, segmentId);
                return segmentId;
            } else {
                logger.info("getNextSegmentId conflict tinyIdInfo:{}", tinyIdInfo);
            }
        }
        throw new TinyIdSysException("get next segmentId conflict");
    }

    public SegmentId convert(TinyIdInfo idInfo) {
        SegmentId segmentId = new SegmentId();
        segmentId.setCurrentId(new AtomicLong(idInfo.getMaxId() - idInfo.getStep()));
        segmentId.setMaxId(idInfo.getMaxId());
        segmentId.setRemainder(idInfo.getRemainder() == null ? 0 : idInfo.getRemainder());
        segmentId.setDelta(idInfo.getDelta() == null ? 1 : idInfo.getDelta());
        // 默认20%加载
        long loading = segmentId.getCurrentId().get() + idInfo.getStep() * Constants.LOADING_PERCENT / 100;
        segmentId.setLoadingId(loading);
        return segmentId;
    }


    public static class TinyIdInfoRowMapper implements RowMapper<TinyIdInfo> {

        @Override
        public TinyIdInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            TinyIdInfo tinyIdInfo = new TinyIdInfo();
            tinyIdInfo.setId(resultSet.getLong("id"));
            tinyIdInfo.setBizType(resultSet.getString("biz_type"));
            tinyIdInfo.setBeginId(resultSet.getLong("begin_id"));
            tinyIdInfo.setMaxId(resultSet.getLong("max_id"));
            tinyIdInfo.setStep(resultSet.getInt("step"));
            tinyIdInfo.setDelta(resultSet.getInt("delta"));
            tinyIdInfo.setRemainder(resultSet.getInt("remainder"));
            tinyIdInfo.setCreateTime(resultSet.getDate("create_time"));
            tinyIdInfo.setUpdateTime(resultSet.getDate("update_time"));
            tinyIdInfo.setVersion(resultSet.getLong("version"));
            return tinyIdInfo;
        }
    }
}
