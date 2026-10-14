package com.rookies6.myspringbootlab.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 1-5) User-defined Properties class (Spring Bean).
 *
 * <p>{@code @Component} registers it as a bean and {@code @ConfigurationProperties}
 * binds every {@code myprop.*} key from application.properties into a matching field.
 * The {@code spring-boot-configuration-processor} dependency generates
 * {@code META-INF/spring-configuration-metadata.json} so the IDE offers
 * auto-completion for these keys.</p>
 */
@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "myprop")
public class MyPropProperties {

    /** maps to {@code myprop.username} */
    private String username;

    /** maps to {@code myprop.port} */
    private int port;
}
