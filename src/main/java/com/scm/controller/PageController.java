package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scm.Repositories.SendMessageRepo;
import com.scm.Services.UserService;
import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.forms.SendMessage;
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

    @Autowired
    private SendMessageRepo SendMessageRepo;

    @Value("${scm.default-profile-image}")
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
    public String registerPage(@RequestParam(value = "email", required = false) String email, Model model) {
        UserForm userForm = new UserForm();

        // Agar URL me email aaya hai, toh form me pehle se set kar do
        if (email != null) {
            userForm.setEmail(email);
        }

        model.addAttribute("userForm", userForm);
        return "register";
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

        User user = new User();

        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setAbout(userForm.getAbout());
        user.setProfilePic(profilePic);
        user.setEnabled(false);
        user.setAbout(userForm.getAbout());
        user.setProvider(Providers.SELF);

        User savedUser = userService.saveUser(user);
        System.out.println("User saved");
        // message = "Registration Successful";
        // add the message
        Message message = Message.builder()
                .content(
                        "Registration successful! A verification link has been sent to your email address. Please verify your account before logging in.")
                .type(MessageType.blue).build();
        session.setAttribute("message", message);
        // redirect to login page
        return "redirect:/login";

    }

    @PostMapping("/contact_submit")
    public String submitContactForm(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam("topic") String topic,
            @RequestParam("message") String message,
            HttpSession session) {

        // 1. Map data to the entity
        SendMessage SendMessage = new SendMessage();
        SendMessage.setName(name);
        SendMessage.setEmail(email);
        SendMessage.setCompany(company);
        SendMessage.setTopic(topic);
        SendMessage.setMessage(message);

        // 2. Save to database
        SendMessageRepo.save(SendMessage);

        // 3. Set success message using your existing Message setup
        session.setAttribute("message",
                Message.builder()
                        .content("Thank you! Your message has been sent successfully.")
                        .type(MessageType.green)
                        .build());

        // 4. Redirect back to the contact page
        return "redirect:/contact";
    }

    // Handles the form submission

}
