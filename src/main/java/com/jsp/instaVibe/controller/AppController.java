package com.jsp.instaVibe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.jsp.instaVibe.dto.User;

import jakarta.validation.Valid;

@Controller
public class AppController {

	@GetMapping({"/","login"})
	public String LoginPage() {
		return "Login.html";
	}
	
	@GetMapping("/register")
	public String RegisterPage(ModelMap map, User user) {
		map.put("user", user);
		return "Register.html";
	}
	
	@PostMapping("/register")
	public String register(@Valid User user,BindingResult result) {
		if(!user.getPassword().equals(user.getConfirmpassword()))
			result.rejectValue("confirmpassword", "error.confirmpassword", "Passwords not Matching");
			
		if(result.hasErrors()) {
			return "Register.html";
		}
		else {
		System.err.println(user);
		return "redirect:https://www.youtube.com";
		}
	}
}

