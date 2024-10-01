package com.kb.wallet.global.config;


import javax.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  public WebAppInitializer() {
    System.out.println("WebAppInitializer created");
  }

  //인코딩 필터 설정
  @Override
  protected Filter[] getServletFilters() {
    CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
    encodingFilter.setEncoding("UTF-8");
    return new Filter[]{encodingFilter};
  }

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[]{SecurityConfig.class, AppConfig.class};
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class[]{WebConfig.class};
  }

  @Override
  protected String[] getServletMappings() {
    return new String[]{"/"};
  }
}

