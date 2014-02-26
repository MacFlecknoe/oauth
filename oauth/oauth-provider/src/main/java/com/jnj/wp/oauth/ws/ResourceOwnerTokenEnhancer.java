package com.jnj.wp.oauth.ws;

import java.util.HashMap;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * <p>
 * Enhances the default Oauth2 access token to include the username of the resource owner.
 * </p>
 * <p>
 * I am not convinced that this is the correct data format for the returned information but we are under a tight deadline.
 * Please note that the added attribute "ext_resource_owner" is <strong>NON-STANDARD</strong>!
 * </p>
 *
 */
public class ResourceOwnerTokenEnhancer implements TokenEnhancer {
	
	public final static String RESOURCE_OWNER = "ext_resource_owner";
	
	public ResourceOwnerTokenEnhancer() {
		// empty
	}
	
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		if(authentication.getPrincipal() instanceof User) {
			
			User user = (User) authentication.getPrincipal();
			
			HashMap<String, Object> additionalInformation = new HashMap<String, Object>();
			additionalInformation.put(RESOURCE_OWNER, user.getUsername());
			
			DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
			result.setAdditionalInformation(additionalInformation);
			
			return result;
		}
		return accessToken;
	}
}
