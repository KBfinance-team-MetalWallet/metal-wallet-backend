package com.kb.wallet.global.config;


import com.kb.wallet.jwt.JwtFilter;
import com.kb.wallet.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"com.kb.wallet.member", "com.kb.wallet.jwt"})
public class SecurityConfig {

  private final TokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;

  // 비밀번호 암호화
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    // UserDetailsService와 PasswordEncoder를 설정
    authenticationManagerBuilder.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
    return authenticationManagerBuilder.build();
  }

  // 클라이언트의 CORS 요청을 허용하는 설정
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:5173"); // 허용할 도메인 설정
    config.addAllowedOrigin("https://kbfinance-team-metalwallet.github.io"); // 허용할 도메인 설정
    config.addAllowedOriginPattern("https://metal-wallet-frontend-*-lee-junrs-projects.vercel.app");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  // 요청 경로에 대한 인증 및 인가 규칙을 정의
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            authorizeHttpRequests -> authorizeHttpRequests.antMatchers("/",
                    "/api/members/register",
                    "/api/members/login",
                    "/members/login",
                    "/api/test/**",
                    "/static/**",
                    "/resources/**",
                    "/public/**",
                    "/webjars/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/swagger-config/**",
                    "/api-docs/**",
                    "/v3/api-docs",
                    "/images/**",
                    "/favicon.ico",
                    "/error",
                    "/api/home",
                    "/api/musicals/**")
                .permitAll()
                .antMatchers("/api/musicals/*/seats-availability").authenticated()
                .antMatchers("/api/musicals/*/schedule/**").authenticated()
                .antMatchers("/api/musicals/*/booking/queue").authenticated()
                .antMatchers("/api/musicals/*/seats/reserve").authenticated()
                .antMatchers("/api/musicals/*/tickets").authenticated()
                .anyRequest().authenticated())
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))

        .headers(headers -> headers.frameOptions(options -> options.sameOrigin()))
        .addFilterBefore(new JwtFilter(tokenProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
