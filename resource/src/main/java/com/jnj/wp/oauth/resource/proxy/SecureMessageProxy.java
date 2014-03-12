package com.jnj.wp.oauth.resource.proxy;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * <p>
 * The proxy accepts calls to an exposed generic "message" server and forwards them to the actual message service after appending the 
 * user's id to the url.
 * </p>
 * <p>
 * The proxy expects the user's id to be placed in the "externalUserId" HTTP header by 
 * the configured processor.
 * </p>
 * 
 * @author mlamber7
 *
 */
public class SecureMessageProxy extends RouteBuilder {
	
	private final Processor processor;
	private final String serviceUrl;
	
	public SecureMessageProxy(Processor processor, String baseUrl) {
		this.processor = processor;
		this.serviceUrl = new StringBuilder(baseUrl)
			.append("message/")
			.append("${header.externalUserId}")
			.append("?throwExceptionOnFailure=false").toString();
	}
	
	@Override
	public void configure() throws Exception {
		from("servlet:///message?matchOnUriPrefix=true")
			.process(processor)
			.setHeader(Exchange.HTTP_URI, simple(serviceUrl))
			.to("http://dummyhost");
	}
}
