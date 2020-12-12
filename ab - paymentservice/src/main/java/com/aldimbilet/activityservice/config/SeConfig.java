package com.aldimbilet.activityservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SeConfig extends WebSecurityConfigurerAdapter
{
	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		// csrf disable to get jwt headers go through
		http.csrf().disable();
		// what the heck is cors ?
		http.cors();
		// The rest of the endpoints may or may not require authentication, depends on your business decisions
		http.authorizeRequests().anyRequest().authenticated();
		// Add jwt authorization filters inside somwhere of the chain
		http.addFilter(new JWTAuthorizationFilter(authenticationManager()));
		// this disables session creation on Spring Security
		// because microservices are idealy stateless, they are jwt authorized
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource()
	{
		// I have no idea what the heck is cors, just copied and pasted it over the internet
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}
}
