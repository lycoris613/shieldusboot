package myspring.di.annot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = { "myspring.di.annot" })
@PropertySource(value={"classpath:value.properties"})
public class HelloConfigComponentScan {
}
