package com.lily.blogservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CircuitBreakConfig {

  @Bean
  public Customizer<Resilience4JCircuitBreakerFactory> searchClientCircuitBreakerConfiguration() {

    TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(4))
        .build();
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofMillis(1000))
        .slidingWindowSize(2)
        .build();

    return factory -> factory.configure(builder -> builder.circuitBreakerConfig(circuitBreakerConfig)
        .timeLimiterConfig(timeLimiterConfig).build(), "searchClientCircuitBreaker");
  }
}
