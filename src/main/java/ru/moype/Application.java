package ru.moype;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;


import org.springframework.web.servlet.LocaleResolver;

@Controller
@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class Application {
	public static void main(String[] args) {
		//SpringApplication.run(Application.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);

		builder.headless(false);

		ConfigurableApplicationContext context = builder.run(args);
		//SingletonBeanRegistry beanRegistry = context.getBeanFactory();
		//beanRegistry.registerSingleton("dbStage", new DBStage());
		
		//AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		//ctx.register(DBStage.class);
		//ctx.register(StageAgent.class);
		//ctx.refresh();		

		
		
	}
	
//	@RequestMapping("/hello")
//	public String showHelloWorld() {
//		return "Hello";	
//	}
	
	public LocaleResolver localeResolver(){
		return new FixedLocaleResolver();
	}
	

	
	
}
