package com.kb.wallet.global.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ProfileInitializer implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    WebApplicationContext ctx = WebApplicationContextUtils
        .getWebApplicationContext(event.getServletContext());

    if (ctx != null) {
      ConfigurableEnvironment env = (ConfigurableEnvironment) ctx.getEnvironment();

      String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
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