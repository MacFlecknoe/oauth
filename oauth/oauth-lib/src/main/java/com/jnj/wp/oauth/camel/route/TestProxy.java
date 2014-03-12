package com.jnj.wp.oauth.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * Example route code.
 * 
 * @author mlamber7
 *
 */
public class TestProxy extends RouteBuilder {
	
	public TestProxy() {
	}
	
	@Override
	public void configure() throws Exception {
		
		from("servlet:///profile?matchOnUriPrefix=true")
		.process(new Processor() {

			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setHeader("externalUserId", "voice");
			}
		})
		.setHeader(Exchange.HTTP_URI, simple("http://google.com/${header.externalUserId}?throwExceptionOnFailure=false"))
		.to("http://dummyhost");
	}
}
