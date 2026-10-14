package com.rookies6.myspringbootlab.config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 1-6) Simple value holder created as a Spring Bean by either
 * {@link ProdConfig} or {@link TestConfig} depending on the active profile.
 */
@Getter
@Builder
@ToString
public class MyEnvironment {

    private String mode;
}
