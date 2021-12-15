package ru.moype.config;

import jade.core.Agent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import net.sf.jade4spring.JadeBean;
import ru.moype.resources.OrderProductionAgent;
import ru.moype.resources.StageAgent;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter{
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }	
	
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/");
        resolver.setSuffix(".html");
        return resolver;
    }
    
	@Bean(name="testBean", initMethod = "startContainer", destroyMethod = "stopContainer")
	public JadeBean jadeBean() {
		JadeBean jadeBean = new JadeBean();
		jadeBean.setUtilityAgents(true);
		jadeBean.setPropertiesFile("classpath:jade.properties");

		return jadeBean;
	}

    @Bean
    public Agent getOrderProductionAgent(){
        return new OrderProductionAgent();
    }

    @Bean
    public Agent getStageAgent(){
        return new StageAgent();
    }
}