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
public class SecureProxy extends RouteBuilder {

	private final Processor processor;
	
	public SecureProxy(Processor processor) {
		this.processor = processor;
	}
	
	public SecureProxy() {
		this.processor = new TestProcessor();
	}
	
	@Override
	public void configure() throws Exception {
		from("servlet:///profile?matchOnUriPrefix=true")
		.process(processor)
		.setHeader(Exchange.HTTP_URI, simple("http://google.com/${header.externalUserId}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false"))
		.to("http://dummyhost");
	}
	
	private static class TestProcessor implements Processor {

		public void process(Exchange exchange) throws Exception {
			exchange.getIn().setHeader("externalUserId", "voice");
		}
	}
}
