package com.scm.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.Services.ContactService;
import com.scm.Services.ImageService;
import com.scm.Services.UserService;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
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
    public String viewContact(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        // load all the user contacts

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";

    }

    @GetMapping("/favourite")
    public String markFavourite(
            @RequestParam("id") String contactId,
            @RequestParam("favourite") boolean favourite,

            @RequestParam(value = "page", defaultValue = "0") int page,

            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            HttpSession session) {

        Contact contact = contactService.getById(contactId);
        contact.setFavourite(favourite);
        contactService.save(contact);

        session.setAttribute("message",
                Message.builder()
                        .content(favourite ? "⭐ Added to favourites!" : "Removed from favourites!")
                        .type(favourite ? MessageType.green : MessageType.red)
                        .build());

        // FIX: same page par redirect
        return "redirect:/user/contacts?page=" + page + "&size=" + size;

    }

    // search handler

    @RequestMapping("/search")
    public String searchhandler(

            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0" + "") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        logger.info("field {} keyword {} ", contactSearchForm.getField(), contactSearchForm.getValue());

        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(
                    contactSearchForm.getValue(), size, page, sortBy, direction, user);
        }

        else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(
                    contactSearchForm.getValue(), size, page, sortBy, direction, user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(
                    contactSearchForm.getValue(), size, page, sortBy, direction, user);

        }

        logger.info("pageContact {}", pageContact);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        return "user/search";

    }

    // delete contact
    @RequestMapping("/delete/{id}")
    public String deleteContact(
            @PathVariable("id") String id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Authentication authentication,
            HttpSession session) {

        System.out.println("Inside delete method");

        // 1. Get the current page elements count from the service to check state
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        Page<Contact> pageContact = contactService.getByUser(user, page, size, "name", "asc");

        logger.info("Attempting to delete contact with ID: '{}'", id);
        // 2. Perform the deletion
        contactService.delete(id);

        // 3. Logic: If only one contact was on the page and it's not the first page
        // (index 0),
        // redirect to the previous page index.
        int redirectPage = page;
        if (pageContact.getNumberOfElements() == 1 && page > 0) {
            redirectPage = page - 1;
        }

        session.setAttribute("message",
                Message.builder()
                        .content("Contact deleted successfully")
                        .type(MessageType.green)
                        .build());

        // Redirect with the corrected page index
        return "redirect:/user/contacts?page=" + redirectPage + "&size=" + size;
    }

}