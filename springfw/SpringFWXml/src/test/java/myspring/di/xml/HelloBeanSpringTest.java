package myspring.di.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:beans.xml")
public class HelloBeanSpringTest {
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	Printer consolePrinter;
	
	@Test
	public void bean1() {
		System.out.println(consolePrinter.getClass().getName());
		
		Hello hello = (Hello) context.getBean("hello1");
		assertEquals("Hello Spring", hello.sayHello());
		hello.print();
		assertEquals("Hello Spring", context.getBean("printer").toString());

		Hello hello2 = context.getBean("hello1", Hello.class);
		hello2.print();
		assertSame(hello, hello2);
				
	}

}