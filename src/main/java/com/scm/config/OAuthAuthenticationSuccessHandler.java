package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.Repositories.UserRepo;
import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthAuthenticationSuccessHandler");

        var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info("Provider: {}", authorizedClientRegistrationId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        // ================================
        // GOOGLE LOGIN
        // ================================
        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {

            String email = oauthUser.getAttribute("email").toString();
            String name = oauthUser.getAttribute("name").toString();
            String picture = oauthUser.getAttribute("picture").toString();

            user.setEmail(email);
            user.setName(name);
            user.setProfilePic(picture);
            user.setProvider(Providers.GOOGLE);
            user.setProviderUserId(oauthUser.getAttribute("sub").toString()); // ✅ Google unique ID
            user.setPassword("oauth2_google_" + UUID.randomUUID()); // ✅ random password
            user.setAbout("This account is created using Google");

            logger.info("Google login - email: {}", email);
        }

        // ================================
        // GITHUB LOGIN
        // ================================
        else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            // 🚨 Koyi email check nahi! Seedha ID nikal kar fake email banao
            String githubId = oauthUser.getAttribute("id").toString();
            String email = "github_" + githubId + "@github-oauth.com";

            String picture = oauthUser.getAttribute("avatar_url").toString();
            String name = oauthUser.getAttribute("login").toString();

            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProviderUserId(githubId);
            user.setProvider(Providers.GITHUB);
            user.setPassword("oauth2_github_" + UUID.randomUUID());
            user.setAbout("This account is created using GitHub");
            logger.info("GitHub login - email forced to: {}", email);
        }

        // ================================
        // FACEBOOK (future)
        // ================================
        else if (authorizedClientRegistrationId.equalsIgnoreCase("facebook")) {
            logger.info("Facebook login - not implemented yet");
        }

        // ================================
        // UNKNOWN PROVIDER
        // ================================
        else {
            logger.info("Unknown provider: {}", authorizedClientRegistrationId);
        }

        // ✅ Save only if user doesn't exist
        userRepo.findByEmail(user.getEmail()).orElseGet(() -> {
            logger.info("New user saved: {}", user.getEmail());
            return userRepo.save(user);
        });

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
}