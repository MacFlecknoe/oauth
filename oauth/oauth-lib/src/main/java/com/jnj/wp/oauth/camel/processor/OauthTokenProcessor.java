package com.jnj.wp.oauth.camel.processor;

import org.apache.camel.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;

/**
 * <p>
 * Pulls the username out of the oauth token and sets it to a standard W+P header for further processing
 * by the end service. The presumption is that the end service will filter the data it returns based on this
 * header.
 * </p>
 * 
 * @author mlamber7
 *
 */
public class OauthTokenProcessor implements Processor {

	public final static String AUTHORIZATION_HEADER = "Authorization";
	// this is the W+P standard header name used to identify end users to service providers (see Ryan Boldman)
	public final static String EXTERNAL_USER_ID_HEADER = "externalUserId";
	
	private final TokenStore store;
	private final String userNameHeader;
	private final boolean removeAuthHeader;
	
	public OauthTokenProcessor(TokenStore store) {
		this(store, EXTERNAL_USER_ID_HEADER, false);
	}
	
	public OauthTokenProcessor(TokenStore store, String userNameHeader, boolean removeAuthHeader) {
		this.store = store;
		this.userNameHeader = userNameHeader;
		this.removeAuthHeader = removeAuthHeader;
	}
	
	public void process(Exchange exchange) throws Exception {
		
		String token = exchange.getIn().getHeader(AUTHORIZATION_HEADER, String.class);
		
		if(!StringUtils.isEmpty(token)) {
			
			String[] tokens = token.split(" ");
			String tokenValue = tokens[1];
			
			OAuth2Authentication authentication = store.readAuthentication(tokenValue);
			String userName = authentication.getName();
			
			if(removeAuthHeader) {
				exchange.getIn().removeHeader(AUTHORIZATION_HEADER);
			}
			exchange.getIn().setHeader(userNameHeader, userName);
		}
	}
}
