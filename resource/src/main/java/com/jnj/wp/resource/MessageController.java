package com.jnj.wp.resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/message")
public class MessageController {

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getMessage(@PathVariable String id) {
		return String.format("Hello \"%s\"", id);
	}
}
