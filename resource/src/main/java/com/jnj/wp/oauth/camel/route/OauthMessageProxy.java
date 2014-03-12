package com.jnj.wp.oauth.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.jnj.wp.oauth.camel.processor.OauthTokenProcessor;

/**
 * Securely wraps the message server with oauth authentication. The proxy accepts calls to an exposed generic "message" server and 
 * forwards it to the actual service after appending the authenticated user's id to the url.
 * 
 * @author mlamber7
 *
 */
public class OauthMessageProxy extends RouteBuilder {
	
	private final OauthTokenProcessor processor;
	private final String url;
	
	public OauthMessageProxy(TokenStore store, String baseUrl) {
		this.processor = new OauthTokenProcessor(store);
		this.url = new StringBuilder(baseUrl).append("message/${header.externalUserId}?throwExceptionOnFailure=false").toString();
	}
	
	@Override
	public void configure() throws Exception {
		from("servlet:///message?matchOnUriPrefix=true")
			.process(processor)
			.setHeader(Exchange.HTTP_URI, simple(url))
			.to("http://dummyhost");
	}
}
