package com.rookies6.myspringbootlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 1-6) Active only when the "prod" profile is set.
 * Registers a {@link MyEnvironment} bean whose mode is the production label.
 */
@Configuration
@Profile("prod")
public class ProdConfig {

    @Bean
    public MyEnvironment myEnvironment() {
        return MyEnvironment.builder()
                .mode("운영환경")
                .build();
    }
}
