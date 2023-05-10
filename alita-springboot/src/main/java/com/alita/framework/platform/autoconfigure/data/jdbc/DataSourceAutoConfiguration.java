package com.alita.framework.platform.autoconfigure.data.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Indexed;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;


@Configuration(proxyBeanMethods = false)
@Indexed
public class DataSourceAutoConfiguration implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceAutoConfiguration.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            PlatformDataBaseMetaInfo dataBaseMetaInfo = new PlatformDataBaseMetaInfo();
            dataBaseMetaInfo.setDatabaseMajorVersion(metaData.getDatabaseMajorVersion());
            dataBaseMetaInfo.setDatabaseMinorVersion(metaData.getDatabaseMinorVersion());
            dataBaseMetaInfo.setDatabaseType(metaData.getDatabaseProductName());
            dataBaseMetaInfo.setDefaultTransactionIsolation(metaData.getDefaultTransactionIsolation());
            dataBaseMetaInfo.setDriverMajorVersion(metaData.getDriverMajorVersion());
            dataBaseMetaInfo.setDriverMinorVersion(metaData.getDriverMinorVersion());
            dataBaseMetaInfo.setDriverName(metaData.getDriverName());
            dataBaseMetaInfo.setDriverVersion(metaData.getDriverVersion());
            dataBaseMetaInfo.setProductName(metaData.getDatabaseProductName());
            dataBaseMetaInfo.setProductVersion(metaData.getDatabaseProductVersion());
            dataBaseMetaInfo.setReadOnly(metaData.isReadOnly());

            logger.info(dataBaseMetaInfo.out());
        } catch (SQLException sqlException) {
            logger.error("Close Connection Exception:{}", sqlException);
        } finally {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException sqlException) {
                logger.error("Close Connection Exception:{}", sqlException);
            }
        }
    }
}
