package com.jnj.wp.oauth.ws;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.AuthorizationRequestManager;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationRequestHolder;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * A non-standard service which allows end users to directly request oauth 2 authorization codes.
 * </p>
 * <p>
 * The authorization codes retrieved from this service will typically be hand input into mobile applications. The mobile apps will 
 * then be able to use these codes to request oauth 2 access tokens per the oauth 2 specification.
 * </p>
 * 
 * @author Michael Lambert
 *
 */
@Controller
@RequestMapping("/oauth/ext/authorization_code")
public class AuthorizationCodeService {

	private final AuthorizationRequestManager requestManager;
	private final AuthorizationCodeServices authCodeservice;
	private final ClientDetailsService clientService;
	private final WebResponseExceptionTranslator translator;
	
	public AuthorizationCodeService(AuthorizationRequestManager manager, 
			AuthorizationCodeServices authCodeservice, 
			ClientDetailsService clientService) {
		this(manager, authCodeservice, clientService, new DefaultWebResponseExceptionTranslator());
	}
	
	public AuthorizationCodeService(AuthorizationRequestManager requestManager, 
			AuthorizationCodeServices authCodeservice, 
			ClientDetailsService clientService, 
			WebResponseExceptionTranslator translator) {
		this.requestManager = requestManager;
		this.authCodeservice = authCodeservice;
		this.clientService = clientService;
		this.translator = translator;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<AuthorizationCodeResponse> create(Principal principal,
			@RequestParam(value = AuthorizationRequest.CLIENT_ID, required = true) String clientId,
			@RequestParam(value = AuthorizationRequest.SCOPE, required = false) String scope,
			@RequestParam Map<String, String> parameters) {
		
		if (!(principal instanceof Authentication)) {
			throw new InsufficientAuthenticationException("User must be authenticated with Spring Security before authorizing an access token.");
		}
		AuthorizationRequestBuilder builder = new AuthorizationRequestBuilder(requestManager, clientService.loadClientByClientId(clientId));
		
		builder.setAuthorizationParameters(parameters);
		builder.getApprovalParameters().put(AuthorizationRequest.USER_OAUTH_APPROVAL, "true");
		builder.setApproved(true);
		
		AuthorizationRequest request = builder.build();
		
		AuthorizationRequestHolder holder = new AuthorizationRequestHolder(request, (Authentication) principal);
		// this used to return the more proper 201 status code but to remain aligned to the Oauth specification now returns a 200
		return new ResponseEntity<AuthorizationCodeResponse>(new AuthorizationCodeResponse(authCodeservice.createAuthorizationCode(holder)), HttpStatus.OK);
	}
	
	@ExceptionHandler(OAuth2Exception.class)
	public ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) throws Exception {
		return translator.translate(e);
	}
	
	@ExceptionHandler(NoSuchClientException.class)
	public ResponseEntity<OAuth2Exception> handleNoSuchClientException(NoSuchClientException e) throws Exception {
		return handleOAuth2Exception(new InvalidRequestException(e.getMessage(), e));
	}
	
	/**
	 * Transforms a {@link ConstraintViolationException} into an {@link OAuth2Exception}. This is done so that clients may handle both types of errors uniformly.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<OAuth2Exception> handleConstraintViolationException(ConstraintViolationException e) throws Exception {
		
		ArrayList<String> errorMessages = new ArrayList<String>();
		
		for(ConstraintViolation<?> violation :e.getConstraintViolations()) {
			errorMessages.add(violation.getMessage());
		}
		return handleOAuth2Exception(new InvalidRequestException(OAuth2Utils.formatParameterList(errorMessages), e));
	}
	
	/**
	 * Wrapper class used to transport an authorization code. A wrapper is used so that additional data may be
	 * returned at a later time without effecting the backward compatibility of the service interface.
	 */
	private static class AuthorizationCodeResponse implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private final String authorizationCode;
		
		public AuthorizationCodeResponse(String code) {
			this.authorizationCode = code;
		}
		
		@JsonProperty("authorization_code")
		@XmlElement(name = "authorization_code")
		public String getAuthorizationCode() {
			return authorizationCode;
		}
	}
}
