package com.kb.wallet.global.config;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class DataSourceConfig {
  @Configuration
  @Profile("dev")
  @PropertySource("classpath:application-dev.properties")
  static class DevConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Bean
    public DataSource dataSource() {
      BasicDataSource dataSource = new BasicDataSource();
      dataSource.setUrl(datasourceUrl);
      dataSource.setUsername(datasourceUsername);
      dataSource.setPassword(datasourcePassword);
      return dataSource;
    }
  }

  @Configuration
  @Profile("prod")
  @PropertySource("classpath:application-prod.properties")
  static class ProdConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Bean
    public DataSource dataSource() {
      BasicDataSource dataSource = new BasicDataSource();
      dataSource.setUrl(datasourceUrl);
      dataSource.setUsername(datasourceUsername);
      dataSource.setPassword(datasourcePassword);
      return dataSource;
    }
  }
}