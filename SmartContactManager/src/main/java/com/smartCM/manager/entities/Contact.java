package com.smartCM.manager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	private String name;
	private String email;
	private String nickName;
	private String image;
	private String phone;
	private String work;
	@Column(length = 1000)
	private String description;
	
	@ManyToOne
	private User user;
	
	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Contact(int cid, String name, String email, String nickName, String image, String phone, String work,
			String description) {
		super();
		this.cid = cid;
		this.name = name;
		this.email = email;
		this.nickName = nickName;
		this.image = image;
		this.phone = phone;
		this.work = work;
		this.description = description;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getDiscription() {
		return description;
	}
	public void setDiscription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "Contact [cid=" + cid + ", name=" + name + ", email=" + email + ", nickName=" + nickName + ", image="
				+ image + ", phone=" + phone + ", work=" + work + ", description=" + description + ", user=" + user
				+ "]";
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
