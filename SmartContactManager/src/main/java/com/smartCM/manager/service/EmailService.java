package com.smartCM.manager.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message, String to) {
		
		boolean f = false;
		
		String from = "gmailName";
		String host="smtp.gmail.com";
		
		Properties properties = System.getProperties();
		System.out.println("Properties : " + properties);
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		Session session = Session.getInstance(properties, new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication("gmailName", "******(gmailPassword)");
			}
		});
		
		session.setDebug(true);
		
		MimeMessage m = new  MimeMessage(session);
		
		try {
			
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);
			m.setText(message);
			Transport.send(m);
			System.out.println("Successfully sent....");
			f = true;
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return f;
	}
}

