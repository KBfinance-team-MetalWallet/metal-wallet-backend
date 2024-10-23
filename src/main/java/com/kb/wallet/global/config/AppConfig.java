package com.kb.wallet.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(DataSourceConfig.class)
@ComponentScan(basePackages = {
    "com.kb.wallet"
})
@PropertySource("classpath:application.properties")
@MapperScan(

    basePackages = {
        "com.kb.wallet.member.repository",
        "com.kb.wallet.ticket.repository",
        "com.kb.wallet.seat.repository",
        "com.kb.wallet.musical.repository"

    },
    annotationClass = org.apache.ibatis.annotations.Mapper.class //해당패키지에서 @Mapper어노테이션이 선언된 인터페이스 찾기
)
@EnableJpaRepositories(basePackages = {
    "com.kb.wallet.member.repository",
    "com.kb.wallet.ticket.repository",
    "com.kb.wallet.seat.repository",
    "com.kb.wallet.musical.repository",
    "com.kb.wallet.account.repository"

})
@EnableJpaAuditing
@EnableTransactionManagement

public class AppConfig {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${spring.datasource.password}")
  private String dbPassword;

  @Value("${spring.datasource.hikari.minimum-idle}")
  private int minimumIdle;

  @Value("${spring.datasource.hikari.maximum-pool-size}")
  private int maximumPoolSize;

  @Value("${spring.datasource.hikari.connection-timeout}")
  private long connectionTimeout;

  @Value("${spring.datasource.hikari.idle-timeout}")
  private long idleTimeout;

  @Value("${spring.datasource.hikari.max-lifetime}")
  private long maxLifetime;

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule()); // LocalDate와 LocalDateTime을 지원
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        false); // 날짜를 타임스탬프가 아닌 ISO 8601 형식으로 출력
    return objectMapper;
  }


  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(driverClassName);
    config.setJdbcUrl(dbUrl);
    config.setUsername(dbUsername);
    config.setPassword(dbPassword);
    config.setConnectionTimeout(connectionTimeout);
    config.setMinimumIdle(minimumIdle);
    config.setMaximumPoolSize(maximumPoolSize);
    config.setIdleTimeout(idleTimeout);
    config.setMaxLifetime(maxLifetime);
    config.setAutoCommit(true);
    return new HikariDataSource(config);
  }

  // JPA 설정
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setDataSource(dataSource);
    emf.setPackagesToScan("com.kb.wallet.member.domain", "com.kb.wallet.ticket.domain",
        "com.kb.wallet.seat.domain",
        "com.kb.wallet.musical.domain", "com.kb.wallet.account.domain");
    emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    // JPA Properties 설정
    Properties jpaProperties = new Properties();
    jpaProperties.put("hibernate.hbm2ddl.auto", "update"); // 테이블 자동 생성
    jpaProperties.put("hibernate.show_sql", "true"); // SQL 쿼리 로그 출력
    jpaProperties.put("hibernate.format_sql", "true"); // SQL 쿼리 포매팅 출력
    jpaProperties.put("hibernate.physical_naming_strategy",
        "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

    emf.setJpaProperties(jpaProperties);

    return emf;
  }

  @Bean
  public PlatformTransactionManager jpaTransactionManager(
      LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return jpaTransactionManager;
  }

  // MyBatis 설정
  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setTypeAliasesPackage("com.kb.wallet.member.domain,"
        + "com.kb.wallet.ticket.domain,"
        + "com.kb.wallet.seat.domain,"
        + "com.kb.wallet.musical.domain");

    sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(
        "classpath*:mapper/**/*.xml"));  // MyBatis 매퍼 설정

    org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
    configuration.setAutoMappingBehavior(
        org.apache.ibatis.session.AutoMappingBehavior.PARTIAL); // Set AUTO_MAPPING_BEHAVIOR to PARTIAL
    configuration.setMapUnderscoreToCamelCase(true);
    sessionFactory.setConfiguration(configuration);

    return sessionFactory.getObject();
  }

  @Bean
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  // MyBatis 트랜잭션 매니저
  @Bean
  public PlatformTransactionManager myBatisTransactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  // 두 트랜잭션 매니저를 ChainedTransactionManager로 묶음
  @Bean
  public PlatformTransactionManager transactionManager(
      @Qualifier("jpaTransactionManager") PlatformTransactionManager jpaTransactionManager,
      @Qualifier("myBatisTransactionManager") PlatformTransactionManager myBatisTransactionManager) {
    return new ChainedTransactionManager(jpaTransactionManager, myBatisTransactionManager);
  }

}