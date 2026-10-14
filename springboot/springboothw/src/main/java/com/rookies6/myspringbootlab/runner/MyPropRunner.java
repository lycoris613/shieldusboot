package com.rookies6.myspringbootlab.runner;

import com.rookies6.myspringbootlab.config.MyEnvironment;
import com.rookies6.myspringbootlab.properties.MyPropProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 1-4) Reads environment variables with {@code @Value}.
 * 1-5) Also receives the {@link MyPropProperties} bean by constructor injection
 *      and prints its getters through an SLF4J {@link Logger}.
 * 1-6) Prints the {@link MyEnvironment} bean when a prod/test profile is active.
 * 1-8) Uses {@code logger.info()} / {@code logger.debug()} instead of
 *      {@code System.out.println()}.
 */
@Component
@RequiredArgsConstructor
public class MyPropRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MyPropRunner.class);

    // 1-4) inject individual keys with @Value
    @Value("${myprop.username}")
    private String username;

    @Value("${myprop.port}")
    private int port;

    // 1-5) inject the whole @ConfigurationProperties bean
    private final MyPropProperties myPropProperties;

    // 1-6) MyEnvironment exists only when the prod or test profile is active
    private final ObjectProvider<MyEnvironment> myEnvironmentProvider;

    @Override
    public void run(String... args) {
        logger.info("================ MyPropRunner start ================");

        // ----- 1-4) values read via @Value -----
        // 1-8) before: System.out.println("Username = " + username);
        logger.info("[@Value] myprop.username = {}", username);
        logger.info("[@Value] myprop.port     = {}", port);

        // ----- 1-5) values read via @ConfigurationProperties bean -----
        logger.info("[@ConfigurationProperties] username = {}", myPropProperties.getUsername());
        logger.info("[@ConfigurationProperties] port     = {}", myPropProperties.getPort());
        logger.debug("[@ConfigurationProperties] bean = {}", myPropProperties);

        // ----- 1-6) MyEnvironment bean chosen by the active profile -----
        MyEnvironment myEnvironment = myEnvironmentProvider.getIfAvailable();
        if (myEnvironment != null) {
            logger.info("[MyEnvironment] mode = {}", myEnvironment.getMode());
            logger.debug("[MyEnvironment] bean = {}", myEnvironment);
        } else {
            logger.info("[MyEnvironment] no bean - run with --spring.profiles.active=prod or test");
        }

        logger.info("================ MyPropRunner end   ================");
    }
}
