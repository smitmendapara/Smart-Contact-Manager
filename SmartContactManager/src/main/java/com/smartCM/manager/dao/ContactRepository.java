package com.smartCM.manager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartCM.manager.entities.Contact;
import com.smartCM.manager.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query("select c from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	
	// for search contact
	public List<Contact> findByNameContainingAndUser(String name, User user);
}
