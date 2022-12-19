package ru.moype;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ToDoSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(
        AuthenticationManagerBuilder auth)throws Exception{
            auth.inMemoryAuthentication()
                    .passwordEncoder(passwordEncoder())
                    .withUser("test")
                    .password(passwordEncoder().encode("test"))
                    .roles("ADMIN", "USER");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
