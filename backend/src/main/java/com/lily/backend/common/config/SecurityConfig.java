package com.lily.backend.common.config;

import com.lily.backend.security.AccessiblePath;
import com.lily.backend.security.jwt.JwtAuthenticationEntryPoint;
import com.lily.backend.security.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Autowired
  private JwtRequestFilter jwtRequestFilter;

  @Autowired
  private JwtAuthenticationEntryPoint entryPoint;

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().antMatchers(
        "/h2-console/**"
        ,"/favicon.ico"
        ,"/error",
        "/swagger-ui/**",
        "/v2/api-docs",
        "/swagger-resources/**",
        "/css/**", "/js/**"
    );
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.cors().and().csrf().disable()
        .exceptionHandling()
        .authenticationEntryPoint(entryPoint)

        // h2-console 을 위한 설정을 추가
        .and()
        .headers()
        .frameOptions()
        .sameOrigin()

        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .antMatchers(AccessiblePath.MAIN).permitAll()
        .antMatchers(AccessiblePath.REGISTER, AccessiblePath.LOGIN).permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

}
