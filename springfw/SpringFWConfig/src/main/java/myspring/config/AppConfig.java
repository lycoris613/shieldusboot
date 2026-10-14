package myspring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Import({DatabaseConfig.class,MyBatisConfig.class})
//@Import({DatabaseConfig.class,MyBatisConfig.class,MvcConfig.class})
@ComponentScan(basePackages = {"myspring.*.service","myspring.*.dao","myspring.aop.annot"})
@EnableAspectJAutoProxy  //<aop:aspectj-autoproxy />
public class AppConfig {

}
