package com.yjh.whatsonmypathweb;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@GetMapping({"/", "/index"})
	public String home(Locale locale, Model model) {		
		return "index";
	}
	
	@GetMapping({"/ex"})
	public String ex() {
		return "ex";
	}
	
}
