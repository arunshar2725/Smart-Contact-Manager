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

        // identify the provider

        var OAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        String authorizedClientRegistrationId = OAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        logger.info(authorizedClientRegistrationId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info(key + ":" + value);
        });

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        // user.setName(oauthUser.getAttribute("name").toString());
        // user.setEmail(oauthUser.getAttribute("email").toString());
        // user.setProvider(Providers.valueOf(authorizedClientRegistrationId.toUpperCase()));
        // user.setProviderUserId(oauthUser.getAttribute("id").toString());
        user.setEmailVerified(true);
        user.setEnabled(true);
        List<String> authorities = List.of(AppConstants.ROLE_USER);
        user.setRoleList(authorities);

        // google login

        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {

            user.setName(oauthUser.getAttribute("name").toString());
            user.setEmail(oauthUser.getAttribute("email").toString());
            // user.setProvider(Providers.GOOGLE);
            user.setProviderUserId(oauthUser.getAttribute("name"));
            user.setProfilePic(oauthUser.getAttribute("picture").toString());
            user.setProvider(Providers.GOOGLE);
            user.setPassword("password");
            user.setAbout("This Account is created using Google");

        }

        // github login

        else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email").toString()
                    : oauthUser.getAttribute("login").toString() + "@gmail.com";
            String picture = oauthUser.getAttribute("avatar_url").toString();
            String name = oauthUser.getAttribute("login").toString();
            String providerUserId = oauthUser.getAttribute("name").toString();

            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProviderUserId(providerUserId);
            user.setProvider(Providers.GITHUB);
            user.setPassword("password");
            user.setAbout("This account is created using GitHub");

        }
        // facebook
        else if (authorizedClientRegistrationId.equalsIgnoreCase("facebook")) {

        }
        // linkedin
        else if (authorizedClientRegistrationId.equalsIgnoreCase("linkedin")) {

        }
        // other
        else {
            logger.info("OAuthAuthenticationSuccessHandler: Unknown Provider");
        }

        // save the user
        /*
         * DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
         * logger.info(user.getName());
         * 
         * user.getAttributes().forEach((key, value) -> {
         * logger.info("{} -> {}", key, value);
         * 
         * });>
         * 
         * logger.info(user.getAuthorities().toString());
         * 
         * // jo data aaya google se use save krna h
         * 
         * String email = user.getAttribute("email").toString();
         * String name = user.getAttribute("name").toString();
         * String picture = user.getAttribute("picture").toString();
         * 
         * // create user and save to database
         * User user1 = new User();
         * user1.setName(name);
         * user1.setEmail(email);
         * user1.setProfilePic(picture);
         * user1.setPassword("password");
         * user1.setUserId(UUID.randomUUID().toString());
         * user1.setProvider(Providers.GOOGLE);
         * user1.setEnabled(true);
         * user1.setEmailVerified(true);
         * user1.setPhoneVerified(true);
         * user1.setProviderUserId(user.getName());
         * user1.setRoleList(List.of(AppConstants.ROLE_USER));
         * user1.setAbout("This user is crested using Google");
         * userRepo.findByEmail(email).orElseGet(() -> userRepo.save(user1));
         * 
         * logger.info("User Saved;" + email);
         */

        userRepo.findByEmail(user.getEmail()).orElseGet(() -> userRepo.save(user));

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

}
