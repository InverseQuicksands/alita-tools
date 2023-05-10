package com.alita.framework.job.config;

import com.alita.framework.job.core.alarmer.JobAlarmer;
import com.alita.framework.job.core.context.JobScheduleContext;
import com.alita.framework.job.core.factory.DefaultMapperFactory;
import com.alita.framework.job.core.factory.MapperFactory;
import com.alita.framework.job.core.scheduler.JobScheduler;
import com.alita.framework.job.repository.*;
import com.alita.framework.job.utils.I18nUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Job server 端配置
 */
@Component
public class JobServerConfig implements InitializingBean, DisposableBean {

    /**
     * 调度线程池最大线程配置【快线程】
     */
    @Value("${job.triggerpool.fast.max:200}")
    private int triggerPoolFastMax;

    /**
     * 调度线程池最大线程配置【慢线程】
     */
    @Value("${job.triggerpool.slow.max:100}")
    private int triggerPoolSlowMax;

    /**
     * 国际化
     */
    @Value("${job.i18n:zh_CN}")
    private String i18n;

    @Value("${job.logretentiondays:30}")
    private int logretentiondays;

    @Autowired
    private JobGroupMapper jobGroupMapper;

    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Autowired
    private JobLogGlueMapper jobLogGlueMapper;

    @Autowired
    private JobLogMapper jobLogMapper;

    @Autowired
    private JobLogReportMapper jobLogReportMapper;

    @Autowired
    private JobRegistryMapper jobRegistryMapper;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobAlarmer jobAlarmer;

    private final JobScheduleContext jobScheduler = new JobScheduler();
    private static MapperFactory mapperFactory;
    private static JobServerConfig jobServerConfig;

    /**
     * spring 容器关闭时销毁创建的线程
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        jobScheduler.destroy();
    }

    /**
     * spring 容器启动初始化相关资源
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        jobServerConfig = this;
        initI18n();             // 初始化 i18n
        initializeMapper();     // 初始化 Mapper 接口工厂
        jobScheduler.initialize();
    }

    /**
     * 初始化 Mapper 接口工厂
     */
    private void initializeMapper() {
        DefaultMapperFactory defaultMapperFactory = new DefaultMapperFactory();
        defaultMapperFactory.setJobGroupMapper(jobGroupMapper);
        defaultMapperFactory.setJobInfoMapper(jobInfoMapper);
        defaultMapperFactory.setJobRegistryMapper(jobRegistryMapper);
        defaultMapperFactory.setJobLogMapper(jobLogMapper);
        defaultMapperFactory.setJobLogGlueMapper(jobLogGlueMapper);
        defaultMapperFactory.setJobLogReportMapper(jobLogReportMapper);
        mapperFactory = defaultMapperFactory;
    }

    /**
     * 初始化 i18n
     *
     * @throws IOException
     */
    public void initI18n() throws IOException {
        String i18nFile = MessageFormat.format("i18n/message_{0}.properties", i18n);
        Resource resource = new ClassPathResource(i18nFile);
        EncodedResource encodedResource = new EncodedResource(resource,"UTF-8");
        I18nUtils.prop = PropertiesLoaderUtils.loadProperties(encodedResource);
    }


    /**
     * 调度线程池最大线程配置【快线程】
     *
     * @return 线程数
     */
    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    /**
     * 调度线程池最大线程配置【慢线程】
     *
     * @return 线程数
     */
    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    /**
     * 日志保留天数.
     *
     * <p>天数必须大于等于7天，否则将一直保留.
     *
     * @return 日志保留天数
     */
    public int getLogretentiondays() {
        if (logretentiondays < 7) {
            return -1;
        }
        return logretentiondays;
    }

    /**
     * 返回数据源【Hikari】连接池
     *
     * @return HikariDataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 返回 Mapper 接口工厂
     *
     * @return MapperFactory
     */
    public static MapperFactory getMapperFactory() {
        return mapperFactory;
    }

    /**
     * 返回当前静态配置类
     *
     * @return JobServerConfig
     */
    public static JobServerConfig getJobServerConfig() {
        return jobServerConfig;
    }

    public JobAlarmer getJobAlarmer() {
        return jobAlarmer;
    }
}
