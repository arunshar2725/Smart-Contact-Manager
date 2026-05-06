package com.scm.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactService {
    // save contacts
    Contact save(Contact contact);

    // update contact
    Contact update(Contact contact);

    // get contacts
    List<Contact> getAll();

    // get contact by id

    Contact getById(String id);

    // delete contact

    void delete(String id);

    // get contacts by userId
    List<Contact> getByUserId(String userId);

    List<Contact> getByUser(User user);

}