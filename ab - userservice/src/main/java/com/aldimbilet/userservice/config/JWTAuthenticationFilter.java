package com.aldimbilet.userservice.config;

import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.aldimbilet.userservice.model.ABUser;
import com.aldimbilet.util.JWTUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager)
	{
		this.authenticationManager = authenticationManager;
		// This is especially important if you don't want to use default /login endpoint of spring security
		// I have configured router in the gateway to route /user/** to this service
		// So this service should respond to calls for /user/** endpoints
		// Also there is no endpoint configured for /login inside the controller, it is created by default by spring security
		setFilterProcessesUrl("/user/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException
	{
		try
		{
			System.err.println("this is attempt");
			ABUser creds = new ObjectMapper().readValue(req.getInputStream(), ABUser.class);
			// This is weird. It throws exceptions for various situations
			// This causes some exceptions get lost in the action
			// That makes the MVC app too much concerned with it's responsbilities
			// But not sure what to do instead of throwing exceptions, maybe return some status code with HttpServletResponse?
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(), creds.getRoles()));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException
	{
		System.err.println("this is unsuccesfull attempt");
		// Some mvc app or other app will get the UNAUTHORIZED status as an indicator
		// DO NOT write body of the response here, keep it as simple as you can
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.getWriter().flush();
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException
	{
		System.err.println("this is succesfull attempt");
		// The user class is the spring framework user by dafault in spring security, not your custom user
		String token = JWT.create().withSubject(((User) auth.getPrincipal()).getUsername()).withExpiresAt(new Date(System.currentTimeMillis() + 900000)).sign(Algorithm.HMAC512(JWTUtils.SECRET_KEY.getBytes()));
		// I have configured it ilke (username) <342h3g349gh93ugb> kinda
		// Ideally you would use some json format
		// This could be anyform you would like as long as you get it in the body of the authentication response
		String body = "(" + ((User) auth.getPrincipal()).getUsername() + ") " + token;
		// Put it inside the response body
		res.getWriter().write(body);
		res.getWriter().flush();
	}
}