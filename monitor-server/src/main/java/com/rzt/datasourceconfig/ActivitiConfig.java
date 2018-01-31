package com.rzt.datasourceconfig;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@SuppressWarnings({"ALL", "AlibabaCommentsMustBeJavadocFormat"})
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "oracleEntityManagerFactory",
        transactionManagerRef = "oracleTransactionManager",
        basePackages = {"com.rzt.repository"}) //设置Repository所在位置
@Configuration
public class ActivitiConfig {

  @Autowired
  PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("oracleDataSource")
    DataSource druidDataSource;

  @Bean
  public SpringProcessEngineConfiguration getProcessEngineConfiguration(){
      SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
      config.setDataSource(druidDataSource);
      config.setTransactionManager(transactionManager);
      config.setDatabaseType("oracle");
      config.setDatabaseSchemaUpdate("true");
      return config;
  }
}
