package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.Repositories.UserRepo;
import com.scm.entities.User;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    // verify email
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {

        User user = userRepo.findByEmailToken(token).orElse(null);

        if (user == null) {
            return "error_page";
        }

        user.setEmailVerified(true);

        user.setEnabled(true);

        user.setEmailToken(null);

        userRepo.save(user);

        return "success_page";
    }

}
