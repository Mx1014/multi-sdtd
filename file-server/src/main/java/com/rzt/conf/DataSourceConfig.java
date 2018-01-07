package com.rzt.conf;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@ComponentScan("com.rzt.conf")
public class DataSourceConfig {

    @Autowired
    private DruidDataSourceProperties properties;

    @Bean(name = "primaryDS", initMethod = "init", destroyMethod = "close")
    @Qualifier("primaryDS")
    @Primary
    //@ConfigurationProperties(prefix="spring.primary.datasource")
    public DataSource primaryDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(properties.getUrl());
        druidDataSource.setUsername(properties.getUsername());
        druidDataSource.setPassword(properties.getPassword());
        druidDataSource.setDriverClassName(properties.getDriverClassName());
        druidDataSource.setInitialSize(properties.getInitialSize());
        druidDataSource.setMaxActive(properties.getMaxActive());
        druidDataSource.setMinIdle(properties.getMinIdle());
        druidDataSource.setMaxWait(properties.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(properties
                .getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(properties
                .getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(properties.getValidationQuery());
        druidDataSource.setTestWhileIdle(properties.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(properties.isTestOnBorrow());
        druidDataSource.setTestOnReturn(properties.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(properties
                .isPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(properties
                .getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setFilters(properties.getFilters());

        try {
            if (null != druidDataSource) {
                druidDataSource.setUseGlobalDataSourceStat(true);
                druidDataSource.init();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "load datasource error, dbProperties is :", e);
        }
        return druidDataSource;
    }


}
