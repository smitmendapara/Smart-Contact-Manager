package com.smartCM.manager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartCM.manager.dao.UserRepository;
import com.smartCM.manager.entities.User;
import com.smartCM.manager.helper.Message;

@Controller
public class MyController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// for home page
	@GetMapping("/")
	public String homeHandler(Model model) {
		
		model.addAttribute("title", "Home Page");
		
		return "home";
	}
	
	// for about page
	@GetMapping("/about")
	public String aboutHandler(Model model) {
		
		model.addAttribute("title", "About Page");
		
		return "about";
	}
	
	// for signUp page
	@GetMapping("/signUp")
	public String signUpHandler(Model model) {
		
		model.addAttribute("title", "SignUp Page");
		model.addAttribute("user", new User());
		
		return "signUp";
	}
	
	// for registering user
	@PostMapping("/do_register")
	public String registerHandler(@Valid @ModelAttribute("user") User user, BindingResult result,
		   @RequestParam(value = "agree", defaultValue = "false") boolean agree, Model model, HttpSession session) {
		
		try {
			
			if(!agree){
				
				System.out.println("You have not agreed the terms and conditions!!");
				throw new Exception("You have not agreed the terms and conditions!!");
			}
			
			if(result.hasErrors()) {
				
				System.out.println("Error is : " + result);
				model.addAttribute("user", user);
		
				return "signUp";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageURL("one.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			userRepository.save(user);
			
			System.out.println("Agreement : " + agree);
			System.out.println("User : " + user);
			
			model.addAttribute("user", new User());
			
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			
			return "signUp";
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));
			
			return "signUp";
		}
	}
	
	// for login page
	@GetMapping("/signin")
	public String loginHandler(Model model) {
		
		model.addAttribute("title", "Login Page");
		
		return "login";
	}
}
