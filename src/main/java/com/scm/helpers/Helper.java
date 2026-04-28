package com.scm.helpers;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        // agar email id password se login kiya h to hum email kaise nikalenge
        if (authentication instanceof OAuth2AuthenticationToken) {

            var aOauthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = aOauthenticationToken.getAuthorizedClientRegistrationId();
            var oauth2User = (OAuth2User) authentication.getPrincipal();
            String username = "";

            if (clientId.equalsIgnoreCase("google")) {

                // sign in with Google to kaise nikalenge
                System.out.println("Getting Email from Google Account");
                username = oauth2User.getAttribute("email").toString();
            }

            else if (clientId.equalsIgnoreCase("github")) {

                // sign in with Github to kaise nikalenge
                System.out.println("Getting Email from Github Account");
                username = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email").toString()
                        : oauth2User.getAttribute("login").toString() + "@gmail.com";
            } else {

                System.out.println("Getting data from local database");
            }

            // sign in with facebook to kaise nikalenge
            return username;
        }

        else {

            return authentication.getName();

        }

    }
}
