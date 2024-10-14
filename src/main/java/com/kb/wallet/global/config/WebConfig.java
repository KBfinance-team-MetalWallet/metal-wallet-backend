package com.kb.wallet.global.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.kb.wallet")
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //Vue.js 빌드 결과물(정적 파일)을 제공할 경로를 설정합니다.
    //Vue.js 빌드 결과물이 /static 폴더에 배치되었다고 가정
    registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/");
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.addPathPrefix("/api", c -> true);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedMethods("*")
        .allowedOrigins("https://kbfinance-team-metalwallet.github.io", "http://localhost:5173")
        .allowedOriginPatterns("*")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
