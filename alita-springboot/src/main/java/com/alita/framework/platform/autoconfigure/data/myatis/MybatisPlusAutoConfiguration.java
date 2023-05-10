/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alita.framework.platform.autoconfigure.data.myatis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <Description>
 * ...
 *
 * @author Zhang Liang
 * @date 2020-06-24 12:01
 * @Version V1.0
 * @since 1.8
 */

//@EnableTransactionManagement
//@Configuration(proxyBeanMethods = false)
//@MapperScan({"org.quicksand.caterpillar.dao"})
@Deprecated
public class MybatisPlusAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusAutoConfiguration.class);


//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
//        // paginationInterceptor.setOverflow(false);
//        // 设置最大单页限制数量，默认 500 条，-1 不受限制
//        // paginationInterceptor.setLimit(500);
//        // 开启 count 的 join 优化,只针对部分 left join
//        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
//        // 设置数据库为mysql,如果是oracle,记得更改
//        paginationInterceptor.setDbType(DbType.POSTGRE_SQL);
//        return paginationInterceptor;
//    }

    /**
     * mybatisplus数据层配置
     * @return DbConfig
     */
//    @Bean
//    public DbConfig dbConfig() {
//        final DbConfig dbConfig = new DbConfig();
//        // 设置ID的生成规则
//        dbConfig.setIdType(IdType.ASSIGN_ID);
//        // 设置表名是否使用下划线命名
//        dbConfig.setTableUnderline(true);
//        // 字段插入非空判断
//        dbConfig.setInsertStrategy(FieldStrategy.NOT_EMPTY);
//        // 字段更新非空判断
//        dbConfig.setUpdateStrategy(FieldStrategy.NOT_EMPTY);
//        // 字段查询非空判断
//        dbConfig.setSelectStrategy(FieldStrategy.NOT_EMPTY);
//        return dbConfig;
//    }

    /**
     * mybatisplus全局配置
     * @param dbConfig
     * @return GlobalConfig
     */
//    @Bean
//    public GlobalConfig globalConfig(DbConfig dbConfig) {
//        final GlobalConfig globalConfig = new GlobalConfig();
//        // 设置mybatisplus数据层配置
//        globalConfig.setDbConfig(dbConfig);
//        // 初始化SqlRunner
//        globalConfig.setEnableSqlRunner(true);
//        // 设置自定义主键ID的生成方式
////        globalConfig.setIdentifierGenerator(customIdGenerator);
//        return globalConfig;
//    }

    /**
     * 配置mybatis
     * @return MybatisConfiguration
     */
//    @Bean
//    public MybatisConfiguration mybatisConfiguration() {
//        final MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
//        // 设置为XML语言驱动
//        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
//        // 设置数据库字段值映射方式,例如：updatedId在进行DB操作的时候会被自动解析为updated_id
//        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
//        // 开启Mybatis的二级缓存
//        mybatisConfiguration.setCacheEnabled(true);
//        // 当查询的返回一行都是null的结果时，MyBatis会帮忙填充一个所有属性都是null的对象
//        mybatisConfiguration.setCallSettersOnNulls(true);
//        // 是否开启自动驼峰命名规则映射:从数据库列名到Java属性驼峰命名的类似映射
//        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
//        // 这个配置会将执行的sql打印出来  或者配置 Slf4jImpl.class
//        mybatisConfiguration.setLogImpl(Slf4jImpl.class);
//
//        return mybatisConfiguration;
//    }


    /**
     * 配置mybatis连接的session工厂
     * @param globalConfig
     * @param mybatisConfiguration
     * @param paginationInterceptor
     * @return
     * @throws Exception MybatisSqlSessionFactoryBean
     */
//    @Bean
//    public MybatisSqlSessionFactoryBean sqlSessionFactory(GlobalConfig globalConfig,
//                                                          MybatisConfiguration mybatisConfiguration,
//                                                          PaginationInterceptor paginationInterceptor,
//                                                          DataSource dataSource,
//                                                          MybatisPlusProperties properties) throws Exception {
//
//        final MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//
//        // 设置数据源
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        // 设置XML的映射路径
////        sqlSessionFactoryBean.setMapperLocations(properties.getMapperLocations());
//        // 设置实体类扫描路径
//        sqlSessionFactoryBean.setTypeAliasesPackage(properties.getTypeAliasesPackage());
//        // 设置mybatisplus全局配置
//        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
//        // 设置mybatis的配置
//        sqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
//        // 设置插件
//        final List<Interceptor> interceptors = new ArrayList<>();
//        // 设置分页插件
//        interceptors.add(paginationInterceptor);
//        // 加载插件
//        sqlSessionFactoryBean.setPlugins(interceptors.get(0));
//        return sqlSessionFactoryBean;
//    }

}
