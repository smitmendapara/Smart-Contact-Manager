package com.smartCM.manager.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartCM.manager.dao.ContactRepository;
import com.smartCM.manager.dao.UserRepository;
import com.smartCM.manager.entities.Contact;
import com.smartCM.manager.entities.User;
import com.smartCM.manager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// common user data for all method 
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);	
		/* System.out.println("User : " + user); */
		model.addAttribute("user", user);
	}
	
	// for dashBoard page
	@RequestMapping("/index")
	public String getDashBoard(Model model) {
		
		model.addAttribute("title", "User Home Page");
		
		return "normal/dashBoard";
	}
	
	// for add contact page 
	@GetMapping("/add-contact")
	public String addContact(Model model) {	
		
		model.addAttribute("title", "Contact Page");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contacts";
	}

	// for save contact with image
	@PostMapping("/process-contact")
	public String getContact(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, 
						     Principal principal, HttpSession session) {
		
		try {
			
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			contact.setUser(user);
			
			if(file.isEmpty()) {
				
				System.out.println("File Not Found!!");
				contact.setImage("contact.png");
			}
			else {
				
				contact.setImage(file.getOriginalFilename());	
				File saveFile = new ClassPathResource("static/img").getFile(); // get object of File
			
				// get path to upload images
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Image Uploaded!!");
			}
			
			// get all contact list from particular user and then add into contact
			user.getContacts().add(contact);
			this.userRepository.save(user); // save data into database
			
			System.out.println("Data : "+contact);
			System.out.println("Added to Data Base");
			
			// alert message using custom message class
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			
			return "normal/add_contacts";
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			System.out.println("Error : "+e.getMessage());
			session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
			
			return "normal/add_contacts";
		}
		
	}
	
	// display view contact with pagination 
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		model.addAttribute("title", "View Contact");
		
		return "normal/show_contacts";
	}
	
	// display particular user contact
	@GetMapping("/{cid}/contact")
	public String showDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		
		System.out.println("CID : " + cid);
		
		Optional<Contact> conOptional = this.contactRepository.findById(cid);
		Contact contact = conOptional.get();
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		// check for only that particular user contact display
		if(user.getId()==contact.getUser().getId()) {
			
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		return "normal/contact_View";
	}
	
	// for delete contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Principal principal, HttpSession session){
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		// check for login and deleted contacts are same user  
		if(user.getId()==contact.getUser().getId()) {
			
			Path path = Paths.get("C:\\Users\\DELL NOTEBOOK\\Documents\\workspace-spring-tool-suite-4-4.10.0.RELEASE\\"
								  + "SmartContactManager\\target\\classes\\static\\img\\"+ contact.getImage());
			try {
				
				Files.delete(path);
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			this.contactRepository.delete(contact);
			session.setAttribute("message", new Message("Successfully contact deleted!!", "alert-success"));
		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	// for update contact
	@PostMapping("/update-form/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		
		Optional<Contact> contactOptional = contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		
		model.addAttribute("contact", contact);
		model.addAttribute("title", "Update Contact");
		
		return "normal/update_Form";
	}
	
	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, 
			Model model, HttpSession session, Principal principal) {
		
		try {
		
			Contact oldContact = contactRepository.findById(contact.getCid()).get();	
		
			if(!file.isEmpty()) {
				
				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();
				
				// set new photo
				File saveFile = new ClassPathResource("static/img").getFile(); // get object of File
				
				// get path to upload images
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
			}
			else {
				
				contact.setImage(oldContact.getImage());
			}
			
			User user = userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Successfully Updated!!", "alert-success"));
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			session.setAttribute("message", new Message("Not Updated!!", "alert-danger"));
		}
		
		System.out.println("ID : " +contact.getCid());
		
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	// for profile page
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "Profile Page");
		
		return "normal/profile";
	}
	
	// for setting page
	@GetMapping("/settings")
	public String openSettings() {
		
		return "normal/settings";
	}
	
	// for accept new password data
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		
		System.out.println("OLDPASSWORD : " + oldPassword);
		System.out.println("NEWPASSWORD : " + newPassword);
		
		String name = principal.getName();
		User currentUser = userRepository.getUserByUserName(name);
		System.out.println(currentUser.getPassword());
		
		if(bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(currentUser);
			
			session.setAttribute("message", new Message("Your Password is Successfully Changed!!", "alert-success"));
			
		}
		else {
			
			session.setAttribute("message", new Message("Enter Correct Old Password!!", "alert-danger"));
			
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
}