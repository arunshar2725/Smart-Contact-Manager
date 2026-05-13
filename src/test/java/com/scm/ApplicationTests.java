package com.scm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scm.Services.EmailService;

@SpringBootTest
class ApplicationTests {

	// @Test
	// void contextLoads() {
	// }

	@Autowired
	private EmailService service;

	@Test
	void sendEmailTest() {
		service.sendEmail("arunshar2725@gmail.com", "Just testing email service", "this is scm project working on ");
	}
}