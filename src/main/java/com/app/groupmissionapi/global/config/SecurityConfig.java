package com.app.groupmissionapi.global.config;

import com.app.groupmissionapi.global.security.CustomAccessDeniedHandler;
import com.app.groupmissionapi.global.security.CustomAuthenticationEntryPoint;
import com.app.groupmissionapi.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {

    return http
      // JWT 기반 Stateless 인증 사용 → 세션/폼 기반 기본 보안 기능 비활성화
      .csrf(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .exceptionHandling(exception -> exception
        .authenticationEntryPoint(customAuthenticationEntryPoint) // 401 custom
        .accessDeniedHandler(customAccessDeniedHandler)           // 403 custom
      )
      .authorizeHttpRequests(auth -> auth
        // 인증 미사용 api
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/uploads/**").permitAll()
        .requestMatchers("/health").permitAll()
        // 나머지 api는 인증 사용
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

}
