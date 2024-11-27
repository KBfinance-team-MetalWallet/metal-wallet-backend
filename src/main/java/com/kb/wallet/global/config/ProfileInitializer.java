package com.kb.wallet.global.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Spring 구동시 설정되는 값 = System property
 * Enviroment로 Spring 설정 값을 읽어옴
 *
 * VM options와 환경 변수에서 원하는 Profile을 설정할 수 있음
 * -Dspring.profiles.active={test}
 * or
 * SPRING_PROFILES_ACTIVE=test (나는 이걸 쓰는 설정으로 밑에 코드를 작성함)
 */
@Slf4j
public class ProfileInitializer implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    WebApplicationContext ctx = WebApplicationContextUtils
        .getWebApplicationContext(event.getServletContext());

    if (ctx != null) {
      ConfigurableEnvironment env = (ConfigurableEnvironment) ctx.getEnvironment();

      String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
      log.info("환경변수 확인: {}", activeProfile);
      if (activeProfile != null && !activeProfile.isEmpty()) {
        env.setActiveProfiles(activeProfile);
      } else {
        env.setActiveProfiles("dev");
      }
    } else {
//      System.out.println("Spring Context 초기화 실패");
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}