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

  /**
   * @Configuration의 중복 처리: DataSourceConfig가 @Configuration을 사용하고 있으므로 Spring은 이 설정 파일들을 스캔할 때
   * @Profile에 따라 적절한 DataSource 설정을 자동으로 선택한다.
   * <p>
   * 프로파일이 "test"일 때 DataSourceConfig.TestConfig가 활성화되고, application-test.properties 파일에서 값을 가져와
   * TestConfig의 dataSource() 메서드가 DataSource 빈으로 등록된다.
   */
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

  @Configuration
  @Profile("test")
  @PropertySource("classpath:application-test.properties")
  static class TestConfig {

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