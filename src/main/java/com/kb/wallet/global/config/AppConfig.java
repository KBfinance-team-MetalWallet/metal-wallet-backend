package com.kb.wallet.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
//    "com.multi.spring2.product",
//    "com.multi.spring2.customer",
//    "com.multi.spring2.order",
})
@MapperScan(
    basePackages = {
//        "com.multi.spring2.product.mapper",
//        "com.multi.spring2.customer.mapper",
//        "com.multi.spring2.order.mapper"
    },
    annotationClass = org.apache.ibatis.annotations.Mapper.class //해당패키지에서 @Mapper어노테이션이 선언된 인터페이스 찾기
)
//TODO: xml 설정으로 변경
@EnableTransactionManagement //transactionManager 사용시 활성화 필요함

public class AppConfig {
  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
    config.setJdbcUrl("jdbc:log4jdbc:mysql://localhost:3306/shop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true");
    config.setUsername("root");
    config.setPassword("1234");

    config.setConnectionTimeout(30000); //풀에서 연결을 가져오기 위해 대기할 최대 시간(밀리초). 기본값은 30,000ms (30초)
    config.setMinimumIdle(1); //풀의 최소 연결 크기입니다. 기본값은 5입니다
    config.setMaximumPoolSize(5);//풀의 최대 크기입니다. 기본값은 10입니다.
    config.setIdleTimeout(600000); //연결이 유휴 상태로 유지될 최대 시간(밀리초)입니다. 기본값은 600,000ms (10분)입니다.
    config.setMaxLifetime(1800000);//풀의 연결이 최대 유지될 시간(밀리초)입니다. 기본값은 1,800,000ms (30분)입니다.
    config.setAutoCommit(true);//커넥션의 자동 커밋 여부를 설정합니다. 기본값은 true입니다.

    return new HikariDataSource(config);
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    //마이바티스 설정
    sessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));


    //mapper용 xml에서 <insert>, <select>태그를 사용하기위한 설정
    // 모든 mapper용 xml
//    sessionFactory.setMapperLocations(new
//        PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml"));
    return sessionFactory.getObject();
  }

  private org.apache.ibatis.session.Configuration getConfiguration() {
    org.apache.ibatis.session.Configuration configuration =
        new org.apache.ibatis.session.Configuration();
    configuration.setMapUnderscoreToCamelCase(true);
//    configuration.getTypeAliasRegistry().registerAlias("Product", com.multi.spring2.product.vo.Product.class);
//    configuration.getTypeAliasRegistry().registerAlias("Customer", com.multi.spring2.customer.vo.Customer.class);

    return configuration;
  }

  @Bean
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}


