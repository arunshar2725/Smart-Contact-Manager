package com.scm.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.Services.ContactService;
import com.scm.Services.ImageService;
import com.scm.Services.UserService;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    // add contact page
    @GetMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {

        // process the form data

        // validate form

        if (result.hasErrors()) {
            session.setAttribute("message",
                    Message.builder().content("Please Correct the following Errors").type(MessageType.red).build());
            return "user/add_contact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);

        // form ---> contact

        User user = userService.getUserByEmail(username);

        // process the contact picture

        // image process

        // upload krne ka code

        String filename = UUID.randomUUID().toString();
        String fileURl = "";

        /// file url main imageService.uploadImage(contactForm.getContactImage(),
        /// filename)

        /// ye bahar ka h

        if (contactForm.getContactImage() != null
                && !contactForm.getContactImage().isEmpty()) {
            // ✅ Image selected - upload it
            fileURl = imageService.uploadImage(contactForm.getContactImage(), filename);
            logger.info("Image uploaded: {}", fileURl);
        } else {
            // ✅ No image - use default
            fileURl = null; // or set a default image URL
            filename = null;
            logger.info("No image selected");
        }

        /// yaha tak

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
        contact.setPicture(fileURl);
        contact.setCloudinaryImagePublicId(filename);
        System.out.println("Contact ID before saving: " + contact.getId());
        contactService.save(contact);

        System.out.println(contactForm);

        // set the contact picture url

        // set message to be displayed on the view

        session.setAttribute("message",
                Message.builder()
                        .content("You have succesfully added a new Contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts/add";
    }

    // view contact
    @RequestMapping
    public String viewContact(Model model, Authentication authentication) {

        // load all the user contacts

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        List<Contact> contacts = contactService.getByUser(user);

        model.addAttribute("contacts", contacts);

        return "user/contacts";

    }

    @GetMapping("/favourite")
    public String markFavourite(
            @RequestParam("id") String contactId,
            @RequestParam("favourite") boolean favourite,
            HttpSession session) {

        Contact contact = contactService.getById(contactId);
        contact.setFavourite(favourite);
        contactService.save(contact);

        session.setAttribute("message",
                Message.builder()
                        .content(favourite ? "⭐ Added to favourites!" : "Removed from favourites!")
                        .type(favourite ? MessageType.green : MessageType.red)
                        .build());

        return "redirect:/user/contacts";
    }

}
