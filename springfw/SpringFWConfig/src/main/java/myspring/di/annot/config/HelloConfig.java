package myspring.di.annot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import myspring.di.annot.ConsolePrinter;
import myspring.di.annot.Hello;
import myspring.di.annot.Printer;
import myspring.di.annot.StringPrinter;


@Configuration
//@ComponentScan(basePackages = { "myspring.di.annot" })
public class HelloConfig {
	//@Bean(autowire = Autowire.BY_NAME)
	@Bean
  public Hello hello() {
      Hello hello = new Hello();
      hello.setName("자바컨피그");
      hello.setPrinter(printer());
      return hello;
  }
	
	@Bean
	@Qualifier("stringPrinter")
	public Printer printer() {
		Printer printer = new StringPrinter();
		return printer;
	}
	
	@Bean
	@Qualifier("consolePrinter")
	public Printer cPrinter() {
		Printer printer = new ConsolePrinter();
		return printer;
	}
}
