package com.jsp.instaVibe.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.jsp.instaVibe.dto.User;
import com.jsp.instaVibe.helper.AES;
import com.jsp.instaVibe.helper.CloudinaryHelper;
import com.jsp.instaVibe.helper.EmailSender;
import com.jsp.instaVibe.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class UserService {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	EmailSender emailSender;
	
	@Autowired
	CloudinaryHelper cloudinaryHelper;

	public String loadRegister(ModelMap map, User user) {
		map.put("user", user);
		return "register.html";
	}

	public String register(@Valid User user, BindingResult result, HttpSession session) {
		if (!user.getPassword().equals(user.getConfirmpassword()))
			result.rejectValue("confirmpassword", "error.confirmpassword", "Passwords not Matching");
		if (repository.existsByEmail(user.getEmail()))
			result.rejectValue("email", "error.email", "Email already Exists");
		if (repository.existsByMobile(user.getMobile()))
			result.rejectValue("mobile", "error.mobile", "Mobile Number Already Exists");
		if (repository.existsByUsername(user.getUsername()))
			result.rejectValue("username", "error.username", "Username already Taken");
		if (result.hasErrors())
			return "register.html";
		else {
			user.setPassword(AES.encrypt(user.getPassword()));
			int otp = new Random().nextInt(100000, 1000000);
			user.setOtp(otp);
			System.err.println(otp);
			emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
			repository.save(user);
			session.setAttribute("pass", "Otp Sent Success");
			return "redirect:/otp/" + user.getId();
		}
	}

	public String verifyOtp(int otp, int id, HttpSession session) {
		User user = repository.findById(id).get();
		if (user.getOtp() == otp) {
			user.setVerified(true);
			user.setOtp(0);
			repository.save(user);
			session.setAttribute("pass", "Account Created Success");
			return "redirect:/login";
		} else {
			session.setAttribute("fail", "Invalid Otp, Try Again!!!");
			return "redirect:/otp/" + id;
		}
	}

	public String resendOtp(int id, HttpSession session) {
		User user =repository.findById(id).get();
		int otp = new Random().nextInt(100000, 1000000);
		user.setOtp(otp);
		System.err.println(otp);
		emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
		repository.save(user);
		session.setAttribute("pass", "Otp Re-Sent Success");
		return "redirect:/otp/" + user.getId();
	}

	public String login(String username, String password, HttpSession session) {
		User user = repository.findByUsername(username);
		if (user == null) {
			session.setAttribute("fail", "Invalid Username");
			return "redirect:/login";
		} else {
			if (AES.decrypt(user.getPassword()).equals(password)) {
				if (user.isVerified()) {
					session.setAttribute("user", user);
					session.setAttribute("pass", "Login Success");
					return "redirect:/home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					user.setOtp(otp);
					System.err.println(otp);
					emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
					repository.save(user);
					session.setAttribute("pass", "Otp Sent Success, First Verify Email to Login");
					return "redirect:/otp/" + user.getId();
				}
			} else {
				session.setAttribute("fail", "Incorrect Password");
				return "redirect:/login";
			}
		}
	}

	public String loadHome(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null)
			return "home.html";
		else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.setAttribute("pass", "Logout Success");
		return "redirect:/login";
	}

	public String profile(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return "profile.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String editProfile(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return "edit-profile.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String updateProfile(HttpSession session, MultipartFile image, String bio) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			user.setBio(bio);
			user.setImageUrl(cloudinaryHelper.saveImage(image));
			repository.save(user);
			return "redirect:/profile";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

}
