package com.jnj.wp.oauth.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.AuthorizationRequestManager;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Builds an AuthorizationRequest with defaults set by a passed {@link ClientDetails} object.
 *
 */
class AuthorizationRequestBuilder {
	
	private final AuthorizationRequestManager requestManager;
	private final ClientDetails clientDetails;
	private Set<String> scope;
	private Set<String> resourceIds;
	private Collection<GrantedAuthority> authorities;
	private Map<String, String> authorizationParameters;
	private Map<String, String> approvalParameters;
	private String redirectUri;
	private boolean isApproved;
	
	public AuthorizationRequestBuilder(AuthorizationRequestManager requestManager, ClientDetails clientDetails) {
		this(requestManager, 
				clientDetails, 
				new HashSet<String>(), 
				new HashSet<String>(), 
				new ArrayList<GrantedAuthority>(), 
				new HashMap<String, String>(), 
				new HashMap<String, String>()
		);
	}
	
	private AuthorizationRequestBuilder(AuthorizationRequestManager requestManager, 
			ClientDetails clientDetails, 
			Set<String> scope, 
			Set<String> resourceIds, 
			Collection<GrantedAuthority> authorities,
			Map<String, String> authorizationParameters, 
			Map<String, String> approvalParameters) {
		
		if(requestManager == null || clientDetails == null) {
			throw new IllegalStateException("requestManager and clientDetails required");
		}
		this.requestManager = requestManager;
		this.clientDetails = clientDetails;
		this.scope = scope;
		this.resourceIds = resourceIds;
		this.authorities = authorities;
		this.approvalParameters = approvalParameters;
		this.authorizationParameters = authorizationParameters;
	}
	
	private AuthorizationRequestManager getAuthorizationRequestManager() {
		return requestManager;
	}
	
	private ClientDetails getClientDetails() {
		return clientDetails;
	}
	
	@NotNull(message="{com.jnj.wp.oauth.ws.AuthorizationRequestBuilder.clientId.notNull.message}")
	public String getClientId() {
		return getClientDetails().getClientId();
	}

	public AuthorizationRequestBuilder setApproved(boolean isApproved) {
		this.isApproved = isApproved;
		return this;
	}
	
	public boolean isApproved() {
		return isApproved;
	}
	
	@NotNull(message="{com.jnj.wp.oauth.ws.AuthorizationRequestBuilder.redirectUri.notNull.message}")
	public String getRedirectUri() {
		if (StringUtils.isEmpty(this.redirectUri) && !CollectionUtils.isEmpty(getClientDetails().getRegisteredRedirectUri())) {
			return getClientDetails().getRegisteredRedirectUri().iterator().next();
		}
		return redirectUri;
	}

	public AuthorizationRequestBuilder setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
		return this;
	}
	
	@Size(min=1, max=10, message="{com.jnj.wp.oauth.ws.AuthorizationRequestBuilder.scope.size.message}")
	public Set<String> getScope() {
		return (CollectionUtils.isEmpty(this.scope)) ? getClientDetails().getScope() : this.scope;
	}
	
	public AuthorizationRequestBuilder setScope(Set<String> scope) {
		this.scope = scope;
		return this;
	}

	public Set<String> getResourceIds() {
		return (CollectionUtils.isEmpty(this.resourceIds)) ? getClientDetails().getResourceIds() : this.resourceIds;
	}

	/**
	 * I am not a fan of this method's signature as I feel it should accept a set of java.net.URI objects. In the spirit
	 * of keeping the oauth interface consistent with spring's oauth implementatioin I have chosen to use strings.
	 */
	public AuthorizationRequestBuilder setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
		return this;
	}
	
	public Collection<GrantedAuthority> getAuthorities() {
		return (CollectionUtils.isEmpty(this.authorities)) ? getClientDetails().getAuthorities() : this.authorities;
	}
	
	public AuthorizationRequestBuilder setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
		return this;
	}
	
	public Map<String, String> getAuthorizationParameters() {
		return authorizationParameters;
	}
	
	public AuthorizationRequestBuilder setAuthorizationParameters(Map<String, String> authorizationParameters) {
		this.authorizationParameters = authorizationParameters;
		return this;
	}
	
	public Map<String, String> getApprovalParameters() {
		return approvalParameters;
	}
	
	public AuthorizationRequestBuilder setApprovalParameters(Map<String, String> approvalParameters) {
		this.approvalParameters = approvalParameters;
		return this;
	}
	
	public AuthorizationRequest build() throws ConstraintViolationException {
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Set<ConstraintViolation<AuthorizationRequestBuilder>> violations = factory.getValidator().validate(this);
		 
		if(!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		DefaultAuthorizationRequest request = new DefaultAuthorizationRequest(getClientId(), getScope());
		
		request.setRedirectUri(getRedirectUri());
		request.setResourceIds(getResourceIds());
		request.setAuthorities(getAuthorities());
		request.setApprovalParameters(getApprovalParameters());
		request.setAuthorizationParameters(getAuthorizationParameters());
		request.setApproved(isApproved());
		
		getAuthorizationRequestManager().validateParameters(getAuthorizationParameters(), getClientDetails());
		
		return request;
	}
}