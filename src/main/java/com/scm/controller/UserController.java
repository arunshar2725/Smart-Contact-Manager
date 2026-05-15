package com.scm.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.Repositories.FeedbackRepo;
import com.scm.Services.ContactService;
import com.scm.Services.ImageService;
import com.scm.Services.UserService;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.FeedbackForm;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")

public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private FeedbackRepo feedbackRepo;

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
    public String userDashboard(Model model, Authentication authentication) {
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        // 1. Database se saare favourite contacts fetch karein
        List<Contact> allFavourites = contactService.getFavouriteContactsByUser(user); // Apna actual method call karein

        if (allFavourites != null && !allFavourites.isEmpty()) {
            // 2. List ko mix (randomize) kar dein
            Collections.shuffle(allFavourites);

            // 3. Sirf starting ke 5 contacts nikaal lein
            List<Contact> random5Favourites = allFavourites.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            model.addAttribute("favouriteContactsList", random5Favourites);
        } else {
            model.addAttribute("favouriteContactsList", null);
        }

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

    @PostMapping("/feedback")
    public String submitFeedback(
            @RequestParam("feedbackType") String feedbackType,
            @RequestParam("subject") String subject,
            @RequestParam("priority") String priority,
            @RequestParam("message") String message,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            Authentication authentication,
            HttpSession session) {

        // 1. Map data to Entity
        FeedbackForm feedbackForm = new FeedbackForm();
        feedbackForm.setFeedbackType(feedbackType);
        feedbackForm.setSubject(subject);
        feedbackForm.setPriority(priority);
        feedbackForm.setMessage(message);

        // 2. Agar user logged in hai, to uski email save kar lo
        if (authentication != null && authentication.isAuthenticated()) {
            feedbackForm.setUserEmail(authentication.getName());
        } else {
            feedbackForm.setUserEmail("Anonymous");
        }

        // 4. Save to Database
        feedbackRepo.save(feedbackForm);

        // 5. Show Success Message on UI
        session.setAttribute("message",
                Message.builder()
                        .content("Thank you! Your feedback has been successfully submitted.")
                        .type(MessageType.green) // Aapka green alert trigger hoga
                        .build());

        // 6. Wapas usi page par bhej do
        return "redirect:/user/feedback"; // Yahan "/feedback" us page ka URL hona chahiye
    }

    // Shows the feedback form page
    @GetMapping("/feedback")
    public String feedbackPage() {
        return "user/feedback"; // This matches feedback.html
    }

}
