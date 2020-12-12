package com.aldimbilet.activityservice.config;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.aldimbilet.util.Constants;
import com.aldimbilet.util.JWTUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter
{
	public JWTAuthorizationFilter(AuthenticationManager authManager)
	{
		// Supply the base class with the authentication manager
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		System.err.println("this is internal filter from act");
		String header = req.getHeader(Constants.HEADER_STRING);
		if (header == null || !header.startsWith(Constants.TOKEN_PREFIX))
		{
			// If no header for Authorization, go ahead and invoke the next filter of tomcat or spring or something
			chain.doFilter(req, res);
			return;
		}
		System.err.println("this is header " + header);
		// the method below retrieves the UsernamePasswordAuthenticationToken of the spring framework from the header of the request
		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
		// The next filter (probably spring security filter somewhere) should get the userpassauthtoken thingy from the context
		// This could ve a header or some request body or something else depending on the httpservletrequest structure
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// Do the next filter, we injected jwt filter to get it from header and to validate and to put it in the context
		chain.doFilter(req, res);
	}

	// Reads the JWT from the Authorization header, and then uses JWT to validate the token
	// Nothing magic
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)
	{
		System.err.println("this is tokenizer");
		String token = request.getHeader(Constants.HEADER_STRING);
		if (token != null)
		{
			// parse the token.
			String user = JWT.require(Algorithm.HMAC512(JWTUtils.SECRET_KEY.getBytes())).build().verify(token.replace(Constants.TOKEN_PREFIX, "")).getSubject();
			if (user != null)
			{
				System.err.println("This is username from token " + user);
				// new arraylist means authorities or roles
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<GrantedAuthority>());
			}
			return null;
		}
		return null;
	}
}