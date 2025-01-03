package com.jsp.instaVibe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsp.instaVibe.dto.User;
import com.jsp.instaVibe.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AppController {
	
	@Autowired
	UserService service;

	@GetMapping({"/","login"})
	public String LoginPage() {
		return "Login.html";
	}
	
	@GetMapping("/register")
	public String RegisterPage(ModelMap map, User user) {
		return service.loadRegister(map, user);
	}
	
	@PostMapping("/register")
	public String register(@Valid User user,BindingResult result) {
		return service.register(user, result);
	}
	
	@GetMapping("/otp/{id}")
	public String loadOtpPage(@PathVariable int id,ModelMap map) {
		map.put("id", id);
		return "user-otp.html";
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam int otp,@RequestParam int id) {
		return service.verifyOtp(otp,id);
	}
}

