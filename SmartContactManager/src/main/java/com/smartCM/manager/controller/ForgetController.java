package com.smartCM.manager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartCM.manager.dao.UserRepository;
import com.smartCM.manager.entities.User;
import com.smartCM.manager.service.EmailService;

@Controller
public class ForgetController {
	
	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/forgot")
	public String forgetPassword() {
		
		return "forgot_password";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		
		System.out.println("Email : " +email);
		
		int otp = random.nextInt(9999);
		System.out.println("OTP : " + otp);
		
		String message = "OTP : "+otp;
        String subject = "Check OTP";
        String to = email;
        
        boolean flag = this.emailService.sendEmail(subject, message, to);
        
        if(flag) {
        	
        	session.setAttribute("OTP", otp);
        	session.setAttribute("email", email);
        	return "verify_otp";
        	
        }
        else {
        	session.setAttribute("message", "Check your Email Id!!");
        	return "forgot_password";
        }
		
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {
		
		int OTP = (int) session.getAttribute("OTP");
		String email = (String) session.getAttribute("email");
		
		if(OTP==otp) {
			
			User user = this.userRepository.getUserByUserName(email);
			if(user==null) {
				
				session.setAttribute("message", "Email not found!!");
				return "forgot_password";
			}
			else {
				
			}
			return "change_password";
		}
		else {
			session.setAttribute("message", "You have enter wrong OTP!!");
			return "verify_otp";
		}
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
		
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password change successfully!!";
	}
}
