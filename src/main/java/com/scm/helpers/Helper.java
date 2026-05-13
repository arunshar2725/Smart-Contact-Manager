package com.scm.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken) {

            var oAuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = oAuthenticationToken.getAuthorizedClientRegistrationId();
            var oauth2User = (OAuth2User) authentication.getPrincipal();
            String username = "";

            if (clientId.equalsIgnoreCase("google")) {
                System.out.println("Getting Email from Google Account");
                username = oauth2User.getAttribute("email").toString();
            }

            else if (clientId.equalsIgnoreCase("github")) {
                System.out.println("Getting Email from Github Account");

                // 🚨 Yahan bhi koyi email check nahi! Seedha fake email return karo
                String githubId = oauth2User.getAttribute("id").toString();
                username = "github_" + githubId + "@github-oauth.com";
            }

            else {
                System.out.println("Getting data from local database");
            }

            return username;
        }

        else {
            return authentication.getName();
        }
    }

    public String getLinkforEmailVerification(String emailToken) {

        String link = "http://localhost:8081/auth/verify-email?token=" + emailToken;
        return link;

    }

}