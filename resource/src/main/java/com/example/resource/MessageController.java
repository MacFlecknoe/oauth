package com.example.resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jnj.wp.oauth.camel.processor.OauthTokenProcessor;

@Controller
public class MessageController {

	@RequestMapping("/message")
	@ResponseBody
	public String getMessage(@RequestHeader(value=OauthTokenProcessor.EXTERNAL_USER_ID_HEADER, required=false) String userName, 
			@RequestParam(value="message", required=true) String message) {
		
		userName = (StringUtils.isEmpty(userName)) ? "anonymous" : userName; 
		
		return String.format("%s said \"%s\"", userName, message);
	}
}
