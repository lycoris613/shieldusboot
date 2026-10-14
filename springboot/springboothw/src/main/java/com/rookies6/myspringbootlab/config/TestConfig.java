package com.rookies6.myspringbootlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 1-6) Active only when the "test" profile is set.
 * Registers a {@link MyEnvironment} bean whose mode is the development label.
 */
@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public MyEnvironment myEnvironment() {
        return MyEnvironment.builder()
                .mode("개발환경")
                .build();
    }
}
