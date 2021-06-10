package com.smartCM.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartCM.manager.dao.UserRepository;
import com.smartCM.manager.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.getUserByUserName(username);
		
		if(user==null) {
			
			throw new UsernameNotFoundException("User could not Found!!");
		}
		
		CustomUserDetails customUser = new CustomUserDetails(user);
		return customUser;
	}

}
