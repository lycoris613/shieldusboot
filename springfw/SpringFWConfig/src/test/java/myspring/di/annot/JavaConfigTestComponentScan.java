package myspring.di.annot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import myspring.di.annot.config.HelloConfigComponentScan;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=HelloConfigComponentScan.class, loader=AnnotationConfigContextLoader.class)
public class JavaConfigTestComponentScan {
	@Autowired
	Hello hello;
	
	@Autowired
	@Qualifier("stringPrinter")
	Printer printer;
	
	@Test
	public void testHello() {
		System.out.println(hello.sayHello());
		hello.print();
		System.out.println(printer.toString());
	}
	
	
}
