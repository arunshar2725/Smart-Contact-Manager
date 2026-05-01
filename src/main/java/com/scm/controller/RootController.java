package com.scm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.Services.UserService;
import com.scm.entities.User;
import com.scm.helpers.Helper;

@ControllerAdvice
public class RootController {
    @Autowired
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(RootController.class);

    @ModelAttribute
    public void addLoggedInUserInformationToModel(Model model, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        System.out.println("Adding Logged In User Information To The Model");

        String username = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("User logged in:{}", username);
        // database me user ka data fetch krenge :get user from database
        User user = userService.getUserByEmail(username);

        System.out.println(user);
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        model.addAttribute("loggedInUser", user);

    }
}
