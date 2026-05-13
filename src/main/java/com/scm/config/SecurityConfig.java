package com.scm.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.config.Customizer;

import com.scm.Services.impl.SecurityCustomUserDetailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

            // formLogin.failureHandler(new AuthenticationFailureHandler() {

            // @Override
            // public void onAuthenticationFailure(HttpServletRequest request,
            // HttpServletResponse response,
            // AuthenticationException exception) throws IOException, ServletException {
            // // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method
            // 'onAuthenticationFailure'");
            // }

            // });

            // formLogin.successHandler(new AuthenticationSuccessHandler() {

            // @Override
            // public void onAuthenticationSuccess(HttpServletRequest request,
            // HttpServletResponse response,
            // Authentication authentication) throws IOException, ServletException {
            // // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method
            // 'onAuthenticationSuccess'");
            // }

            // });

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

    // user create and login using java code with in memory service

    // @Bean
    // public UserDetailsService userDetailsService() {

    // UserDetails user1 = User.withDefaultPasswordEncoder().username("admin123")
    // .password("admin123")
    // .roles("ADMIN", "USER")
    // .build();

    // UserDetails user2 = User.withDefaultPasswordEncoder().username("user123")
    // .password("password")
    // // .roles("ADMIN", "USER")
    // .build();

    // var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1,
    // user2);
    // return inMemoryUserDetailsManager;

    // }

}
