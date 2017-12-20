package com.rzt.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class PrimaryDataSource {
    private Logger logger = LoggerFactory.getLogger(PrimaryDataSource.class);
    @Value("${spring.datasource.url}")
    private String dbUrlMy;

    @Value("${spring.datasource.username}")
    private String usernameMy;

    @Value("${spring.datasource.password}")
    private String passwordMy;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassNameMy;

    @Value("${spring.datasource.initialSize}")
    private int initialSizeMy;

    @Value("${spring.datasource.minIdle}")
    private int minIdleMy;

    @Value("${spring.datasource.maxActive}")
    private int maxActiveMy;

    @Value("${spring.datasource.maxWait}")
    private int maxWaitMy;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillisMy;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillisMy;

    @Value("${spring.datasource.validationQuery}")
    private String validationQueryMy;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdleMy;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrowMy;

    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturnMy;

    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatementsMy;

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSizeMy;

    @Value("${spring.datasource.filters}")
    private String filtersMy;

    @Value("${spring.datasource.connectionProperties}")
    private String connectionPropertiesMy;

    @Bean(name = "oracleDataSource")
    @Primary
//    @ConfigurationProperties(prefix = "spring.datasourceMysql")
    public DataSource mysqlDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrlMy);
        datasource.setUsername(usernameMy);
        datasource.setPassword(passwordMy);
//        datasource.setDriverClassName(driverClassNameMy);

        //configuration
        datasource.setInitialSize(initialSizeMy);
        datasource.setMinIdle(minIdleMy);
        datasource.setMaxActive(maxActiveMy);
        datasource.setMaxWait(maxWaitMy);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillisMy);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillisMy);
        datasource.setValidationQuery(validationQueryMy);
        datasource.setTestWhileIdle(testWhileIdleMy);
        datasource.setTestOnBorrow(testOnBorrowMy);
        datasource.setTestOnReturn(testOnReturnMy);
        datasource.setPoolPreparedStatements(poolPreparedStatementsMy);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSizeMy);
        try {
            datasource.setFilters(filtersMy);
        } catch (SQLException e) {
            logger.error("druid 配置初始化过滤器", e);
        }
        datasource.setConnectionProperties(connectionPropertiesMy);
        return datasource;
    }
}
