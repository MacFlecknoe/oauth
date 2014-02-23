package com.jnj.wp.security.externaluser;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

/**
 * <p>
 * There seems to be a bug in the implementation of {@link RequestHeaderAuthenticationFilter} in that it does not
 * defer to a configured {@link AuthenticationEntryPoint} to handle an unauthorized access attempt. This class merely
 * wraps {@link RequestHeaderAuthenticationFilter} in order to provide that additional behavior.
 * </p>
 * <p>
 * Without this wrapper a 500 error is returned to the end user as the {@link PreAuthenticatedCredentialsNotFoundException} 
 * exception bubbles up out of the web service.
 * </p>
 * 
 * @author mlamber7
 *
 */
public class RequestHeaderAuthenticationFilterWrapper extends GenericFilterBean {

	private RequestHeaderAuthenticationFilter filter;
	private AuthenticationEntryPoint entryPoint;

	public RequestHeaderAuthenticationFilterWrapper(RequestHeaderAuthenticationFilter filter) {
		this(filter, new Http403ForbiddenEntryPoint());
	}
	
	public RequestHeaderAuthenticationFilterWrapper(RequestHeaderAuthenticationFilter filter, AuthenticationEntryPoint entryPoint) {
		this.filter = filter;
		this.entryPoint = entryPoint;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		
		try {
			filter.doFilter(request, response, chain);
		} catch (PreAuthenticatedCredentialsNotFoundException e) {
			entryPoint.commence(request, response, e);
		}
	}
}
