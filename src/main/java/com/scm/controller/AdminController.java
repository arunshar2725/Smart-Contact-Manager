package com.scm.controller;

import com.scm.Repositories.ContactRepo;
import com.scm.Repositories.UserRepo;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.helpers.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ContactRepo contactRepo;

    // Sidebar aur Navbar ke liye Logged-in Admin ka data inject karna
    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = Helper.getEmailOfLoggedInUser(authentication);
            User loggedInUser = userRepo.findByEmail(username).orElse(null);
            model.addAttribute("loggedInUser", loggedInUser);
        }
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // 1. Fetching real data
        List<User> allUsers = userRepo.findAll();

        // 2. Calculating practical stats
        long totalUsers = allUsers.size();
        long totalContacts = contactRepo.count();
        long activeUsers = allUsers.stream().filter(User::isEnabled).count();
        long oauthUsers = allUsers.stream().filter(u -> !u.getProvider().name().equalsIgnoreCase("SELF")).count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("oauthUsers", oauthUsers);

        // 3. Mapping data to lightweight DTOs for the UI table
        List<User> reversedUsers = allUsers.stream().collect(Collectors.toList());
        Collections.reverse(reversedUsers); // Latest users first

        List<AdminUserDto> usersList = reversedUsers.stream().map(user -> {
            String role = user.getRoleList().contains("ROLE_ADMIN") ? "ADMIN" : "USER";
            String pic = (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) ? user.getProfilePic()
                    : "/images/telephone.png";
            return new AdminUserDto(user.getUserId(), pic, user.getName(), user.getEmail(), role,
                    user.getProvider().name(), user.isEnabled());
        }).collect(Collectors.toList());

        // Pass full list for main table, and top 5 for "Recent Users" widget
        model.addAttribute("users", usersList);
        model.addAttribute("recentUsers", usersList.stream().limit(5).collect(Collectors.toList()));

        return "admin/dashboard";
    }

    // --- Lightweight DTO (Frontend ko clean rakhne ke liye) ---
    public static class AdminUserDto {
        public String id, profilePic, name, email, role, provider;
        public boolean enabled;

        public AdminUserDto(String id, String profilePic, String name, String email, String role, String provider,
                boolean enabled) {
            this.id = id;
            this.profilePic = profilePic;
            this.name = name;
            this.email = email;
            this.role = role;
            this.provider = provider;
            this.enabled = enabled;
        }
    }
    // Import zaroor kijiyega

    // ====================================================================
    // ACTION: BLOCK / UNBLOCK USER (Toggle Status)
    // ====================================================================
    @GetMapping("/users/toggle-status/{id}")
    @Transactional
    public String toggleUserStatus(@PathVariable("id") String userId, jakarta.servlet.http.HttpSession session) {
        User user = userRepo.findById(userId).orElse(null);

        if (user != null) {
            boolean newStatus = !user.isEnabled();
            user.setEnabled(newStatus);
            userRepo.save(user); // Force save

            String action = newStatus ? "Activated" : "Blocked";
            session.setAttribute("message", Message.builder()
                    .content("User account has been successfully " + action + "!")
                    .type(newStatus ? MessageType.green : MessageType.red)
                    .build());
        }
        return "redirect:/admin/dashboard";
    }

    // ====================================================================
    // ACTION: DELETE USER (Force Delete with Transaction)
    // ====================================================================
    @GetMapping("/users/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable("id") String userId, jakarta.servlet.http.HttpSession session) {
        User user = userRepo.findById(userId).orElse(null);

        if (user != null) {
            try {
                // MySQL Foreign Key Constraint Error se bachne ke liye
                // Pehle is user ke saare contacts ko force delete karenge
                if (user.getContacts() != null && !user.getContacts().isEmpty()) {
                    contactRepo.deleteAll(user.getContacts());
                }

                // Phir finally User ko database se uda denge
                userRepo.delete(user);

                session.setAttribute("message", Message.builder()
                        .content("User and all their related data deleted permanently!")
                        .type(MessageType.green)
                        .build());
            } catch (Exception e) {
                session.setAttribute("message", Message.builder()
                        .content("Error deleting user: " + e.getMessage())
                        .type(MessageType.red)
                        .build());
            }
        }
        return "redirect:/admin/dashboard";
    }

    // 1. Export Users (CSV)
    @GetMapping("/users/export")
    public void exportUsers(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");

        List<User> users = userRepo.findAll();
        // Simple CSV writing
        var writer = response.getWriter();
        writer.println("Name,Email,Provider,Role,Status");
        for (User u : users) {
            writer.println(u.getName() + "," + u.getEmail() + "," + u.getProvider() + "," + "USER" + ","
                    + (u.isEnabled() ? "Active" : "Blocked"));
        }
    }

    // 2. View Details (Redirect to profile page)
    @GetMapping("/users/view/{id}")
    public String viewUserDetails(@PathVariable("id") String id, Model model) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "admin/user_details"; // Ek naya simple page bana lijiye
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        // Database se saare users fetch karo
        List<User> users = userRepo.findAll();
        // Model mein map karo
        model.addAttribute("users", users);
        return "admin/all_users"; // File: templates/admin/all_users.html
    }
}