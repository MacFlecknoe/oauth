package com.example.resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageController {
	
	private static final String EXTERNAL_USER_ID = "externalUserId";
	private static final String MESSAGE = "message";

	@RequestMapping("/message")
	@PreAuthorize("#oauth2.clientHasRole('ROLE_CLIENT')")
	@ResponseBody
	public String getMessage(@RequestHeader(value=EXTERNAL_USER_ID, required=false) String userName, 
			@RequestParam(value=MESSAGE, required=true) String message) {
		
		userName = (StringUtils.isEmpty(userName)) ? "anonymous" : userName; 
		
		return String.format("%s said \"%s\"", userName, message);
	}
}
