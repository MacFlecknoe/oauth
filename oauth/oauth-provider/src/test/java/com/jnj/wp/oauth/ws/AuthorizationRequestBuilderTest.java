package com.jnj.wp.oauth.ws;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.AuthorizationRequestManager;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequestManager;
import org.springframework.security.oauth2.provider.InMemoryClientDetailsService;

public class AuthorizationRequestBuilderTest {
	
	private ClientDetailsService clientDetailsService;
	private AuthorizationRequestManager requestManager;

	@Before
	public void setUp() throws Exception {
		
		ClientDetails testClient = new BaseClientDetails(
				"test_client", "test", "read,write", "implicit", "ROLE_CLIENT", "test://test"
		);
		ClientDetails badClientMissingRedirect = new BaseClientDetails(
				"bad_client_missing_redirect", "test", "read,write", "implicit", "ROLE_CLIENT", null
		);
		ClientDetails badClientMissingScope = new BaseClientDetails(
				"bad_client_missing_scope", "test", null, "implicit", "ROLE_CLIENT", null
		);
		Map<String, ClientDetails> clientDetailsStore = new HashMap<String, ClientDetails>();
		clientDetailsStore.put(testClient.getClientId(), testClient);
		clientDetailsStore.put(badClientMissingRedirect.getClientId(), badClientMissingRedirect);
		clientDetailsStore.put(badClientMissingScope.getClientId(), badClientMissingScope);
		
		InMemoryClientDetailsService testDetailsService = new InMemoryClientDetailsService();
		testDetailsService.setClientDetailsStore((Map<String, ? extends ClientDetails>) clientDetailsStore);
		
		this.requestManager = new DefaultAuthorizationRequestManager(clientDetailsService);
		this.clientDetailsService = testDetailsService;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefaultValues() {
		
		ClientDetails details = clientDetailsService.loadClientByClientId("test_client");
		AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(requestManager, details);
		
		assertNotNull(builder.build());
	}

	@Test(expected = javax.validation.ConstraintViolationException.class)
	public void testMissingRedirectUri() {
		
		ClientDetails details = clientDetailsService.loadClientByClientId("bad_client_missing_redirect");
		AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(requestManager, details);
		
		builder.build();
	}
	
	@Test
	public void testOverrideRedirectUri() {
		
		ClientDetails details = clientDetailsService.loadClientByClientId("test_client");
		AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(requestManager, details);
		builder.setRedirectUri("override://test");
		
		AuthorizationRequest request = builder.build();
		
		assertNotNull(request);
		assertEquals("override://test", request.getRedirectUri());
	}
	
	@Test(expected = javax.validation.ConstraintViolationException.class)
	public void testMissingScope() {
		
		ClientDetails details = clientDetailsService.loadClientByClientId("bad_client_missing_scope");
		AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(requestManager, details);
		
		builder.build();
	}
	
	@Test(expected = java.lang.IllegalStateException.class)
	public void testMissingRequestManager() {
		
		ClientDetails details = clientDetailsService.loadClientByClientId("test_client");
		AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(null, details);
		
		builder.build();
	}
	
	@Test(expected = java.lang.IllegalStateException.class)
	public void testMissingClientDetails() {
		AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(requestManager, null);
		builder.build();
	}
	
	@Test
	public void testConstraintViolationSize() {
		
		try {
			ClientDetails details = clientDetailsService.loadClientByClientId("test_client");
			AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(requestManager, details);
			builder.build();
		
		} catch(javax.validation.ConstraintViolationException e) {
			assertEquals(3, e.getConstraintViolations().size());
		}
	}
}
