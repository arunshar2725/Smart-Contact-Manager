package com.scm.handlers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.scm.helpers.Message;
import com.scm.helpers.MessageType;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exc,
            HttpServletRequest request,
            HttpSession session) {

        // CHANGE THIS: Keep the message strictly to your 2MB business rule
        session.setAttribute("message",
                Message.builder()
                        .content("File size must be less than 2MB!")
                        .type(MessageType.red)
                        .build());

        String referer = request.getHeader("Referer");
        if (referer != null) {
            return "redirect:" + referer;
        }

        return "redirect:/user/contacts/add";
    }
}