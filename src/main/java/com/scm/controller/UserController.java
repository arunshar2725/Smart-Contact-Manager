package com.scm.controller;

import java.security.Principal;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.Services.ContactService;
import com.scm.Services.ImageService;
import com.scm.Services.UserService;
import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")

public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ContactService contactService;

    @ModelAttribute
    public void addLoggedInUserInformationToModel(Model model, Authentication authentication) {
        System.out.println("Adding Logged In User Information To The Model");

        String username = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("User logged in:{}", username);
        // database me user ka data fetch krenge :get user from database
        User user = userService.getUserByEmail(username);
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        model.addAttribute("loggedInUser", user);

    }

    // user dashboard page
    @RequestMapping(value = "/dashboard")
    public String userDashboard() {
        System.out.println("user dashboard page");
        return "user/dashboard";
    }

    // edit page link
    @RequestMapping("/edit_profile")
    public String userProfileEdit(Model model, Authentication authentication) {

        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        model.addAttribute("user", user);

        return "user/edit_profile";
    }

    // user profile page
    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication) {
        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        long favouriteContacts = contactService.countFavouriteByUser(user);
        long totalContacts = contactService.countByUser(user);

        model.addAttribute("totalContacts", totalContacts);

        model.addAttribute("favouriteContacts", favouriteContacts);

        return "user/profile";

    }

    // user add contact page

    // user view contact

    // user edit contact

    // user delete contact

    // user search contact

    @PostMapping("/update_profile")
    public String updateProfile(
            @ModelAttribute User user, // This holds the updated name, phone, and about from the form
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Authentication authentication,
            HttpSession session) {

        // 1. Fetch the completely loaded existing user from the database
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User existingUser = userService.getUserByEmail(username);

        // 2. Update ONLY the fields we allow the user to edit
        existingUser.setName(user.getName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setAbout(user.getAbout());

        // 3. Process the profile picture exactly like you do in Contacts
        if (profileImage != null && !profileImage.isEmpty()) {
            logger.info("Uploading new profile picture...");
            String filename = UUID.randomUUID().toString();
            String fileUrl = imageService.uploadImage(profileImage, filename);

            existingUser.setProfilePic(fileUrl);
            // If your User entity tracks Cloudinary IDs, uncomment this:
            // existingUser.setCloudinaryImagePublicId(filename);
        } else {
            logger.info("No new image selected. Keeping the old one.");
        }

        // 4. CALL YOUR EXISTING METHOD
        // Since 'existingUser' has its ID, your Impl will find it and securely update
        // it!
        Optional<User> updatedUser = userService.updateUser(existingUser);

        if (updatedUser.isPresent()) {
            logger.info("User profile successfully updated in DB: {}", updatedUser.get().getName());

            session.setAttribute("message",
                    Message.builder()
                            .content("Profile updated successfully!")
                            .type(MessageType.green)
                            .build());
        } else {
            session.setAttribute("message",
                    Message.builder()
                            .content("Something went wrong while updating the profile.")
                            .type(MessageType.red)
                            .build());
        }

        return "redirect:/user/profile";
    }
}
