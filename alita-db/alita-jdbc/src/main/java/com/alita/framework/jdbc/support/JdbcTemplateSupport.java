package com.alita.framework.jdbc.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据库操作类.
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 00:43
 **/
@Component
public class JdbcTemplateSupport {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;


    public <T> T queryForObject(String sql, Map<String, Object> map, Class<T> tClass) {
        SqlParameterSource sqlParameterSource = buildSqlParameterSource(map);
        return namedJdbcTemplate.queryForObject(sql, sqlParameterSource, tClass);
    }

    public <T> T queryForObject(String sql, Map<String, Object> map, RowMapper<T> rowMapper) {
        SqlParameterSource sqlParameterSource = buildSqlParameterSource(map);
        return namedJdbcTemplate.queryForObject(sql, sqlParameterSource, rowMapper);
    }

    public <T> List<T> queryForList(String sql, Map<String, Object> map, Class<T> tClass) {
        SqlParameterSource sqlParameterSource = buildSqlParameterSource(map);
        return namedJdbcTemplate.queryForList(sql, sqlParameterSource, tClass);
    }

    public int store(String sql, Map<String, Object> map) {
        SqlParameterSource sqlParameterSource = buildSqlParameterSource(map);
        return namedJdbcTemplate.update(sql, sqlParameterSource);
    }

    public int[] batchSave(String sql, List batchArgs) {
        SqlParameterSource[] parameterSources = SqlParameterSourceUtils.createBatch(batchArgs.toArray());
        return namedJdbcTemplate.batchUpdate(sql, parameterSources);
    }

    private SqlParameterSource buildSqlParameterSource(Object parameter) {
        SqlParameterSource sqlParameterSource;
        if (ParamType.isPassiveType(parameter)) {
            sqlParameterSource = new MapSqlParameterSource("value", parameter);
        } else {
            if (ParamType.isMap(parameter)) {
                sqlParameterSource = new MapSqlParameterSource((Map) parameter);
            } else {
                sqlParameterSource = new BeanPropertySqlParameterSource(parameter);
            }
        }
        return sqlParameterSource;
    }

}
