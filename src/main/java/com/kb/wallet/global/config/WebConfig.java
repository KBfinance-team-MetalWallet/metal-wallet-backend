package com.kb.wallet.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.kb.wallet")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Vue.js 빌드 결과물(정적 파일)을 제공할 경로를 설정합니다.
        //Vue.js 빌드 결과물이 /static 폴더에 배치되었다고 가정
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");
    }
}
