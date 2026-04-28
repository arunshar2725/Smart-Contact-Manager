package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.Services.UserService;
import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageController {

    @Value("${scm.manager.upload-dir}")
    private String profilePic;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        // Model ka use krke hum data ko template me bhej skte hai, jise hum home.html
        // me use kr skte hai.
        // sending data from model to view
        System.out.println("home page");
        model.addAttribute("name", "Substrings Technology");
        model.addAttribute("instagram", "arunsharma._07");
        model.addAttribute("githubrepo", "https://github.com/arunshar2725/arunsharmademo");
        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About Page Landing");
        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("Services Page Landing");
        return "services";
    }

    @RequestMapping("/contact")
    public String contactPages() {
        System.out.println("Contact Page Loading");
        return new String("contact");
    }

    // this is for login = view
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

    // Registration page
    @GetMapping("/register")
    public String register(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return ("register");
    }
    // processing register

    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {
        System.out.println("process register");

        // we have to fetch the data
        System.out.println(userForm);
        // validate form data
        if (rBindingResult.hasErrors()) {
            return "register";
        }
        // todo : Valdate userfoem(NexVideo)

        // send data to database

        // userservice

        // User user = User.builder()
        // .name(userForm.getName())
        // .email(userForm.getEmail())
        // .password(userForm.getPassword())
        // .phoneNumber(userForm.getPhoneNumber())
        // .about(userForm.getAbout())
        // .profilePic(profilePic)
        // .build();

        User user = new User();

        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setAbout(userForm.getAbout());
        user.setProfilePic(profilePic);
        user.setAbout(userForm.getAbout());
        user.setProvider(Providers.SELF);

        User savedUser = userService.saveUser(user);
        System.out.println("User saved");
        // message = "Registration Successful";
        // add the message
        Message message = Message.builder().content("Registration Successful").type(MessageType.blue).build();
        session.setAttribute("message", message);
        // redirect to login page
        return "redirect:/register";

    }

}
