package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.Services.ContactService;
import com.scm.Services.UserService;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.helpers.Helper;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @Autowired
    public UserService userService;

    // add contact page
    @GetMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@ModelAttribute ContactForm contactForm, Authentication authentication) {

        // process the form data

        // validate form

        String username = Helper.getEmailOfLoggedInUser(authentication);

        // form ---> contact

        User user = userService.getUserByEmail(username);

        // process the contact picture

        // save to database

        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setPicture(contactForm.getPicture());
        contact.setFavourite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedinLink(contactForm.getLinkedInLink());
        contact.setUser(user);
        contactService.save(contact);

        System.out.println(contactForm);

        // set the contact picture url

        // set message to be displayed on the view

        return "redirect:/user/contacts/add";
    }
}
