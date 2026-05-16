package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import com.scm.Services.impl.SecurityCustomUserDetailService;



@Configuration
public class SecurityConfig {

    @Autowired
    private AuthFailureHandler authFailureHandler;

    @Autowired
    private OAuthAuthenticationSuccessHandler handler;

    @Autowired
    private SecurityCustomUserDetailService userDetailService;

    // configuration of authentication provider for spring security

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailService);

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // configuartion
        // url configuration kiya h kon public hogi kon private hogi
        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize
                    .requestMatchers("/user/**").authenticated();

            authorize.anyRequest().permitAll();
        });

        // form default login
        // agar kuch change krna ho to hum yaha aayenge:form login se related
        httpSecurity.formLogin(formLogin -> {

            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate");
            formLogin.successForwardUrl("/user/profile");
            // formLogin.failureUrl("/login?error=true");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");

            formLogin.failureHandler(authFailureHandler);

        });
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.logout(logoutform -> {
            logoutform.logoutUrl("/logout");

            logoutform.logoutSuccessUrl("/login?logout=true");

        });

        // oauth configure

        httpSecurity.oauth2Login(oauth2 -> {

            oauth2.loginPage("/login");
            oauth2.successHandler(handler);
        });

        return httpSecurity.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
