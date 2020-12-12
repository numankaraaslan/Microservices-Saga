package com.aldimbilet.eureka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SeConfig extends WebSecurityConfigurerAdapter
{
	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		// CSRF disable to be able to pass jwt tokens through
		// Any request to eureka must set username and password for eureka, especially for console
		http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
	}
}
