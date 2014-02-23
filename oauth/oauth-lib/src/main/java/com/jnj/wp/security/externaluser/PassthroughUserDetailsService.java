package com.jnj.wp.security.externaluser;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * <p>
 * {@link UserDetailsService} that creates a {@link UserDetails} instance using only the username passed into the framework.
 * </p>
 * <p>
 * This is for pre-authorized users.
 * </p>
 * 
 * @author mlamber7
 *
 */
public class PassthroughUserDetailsService implements UserDetailsService {

	public PassthroughUserDetailsService() {
		// empty
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		return new User(username, StringUtils.EMPTY, authorities);
	}

}
