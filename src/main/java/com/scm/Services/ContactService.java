package com.scm.Services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction);

    // search contact

    Page<Contact> searchByName(String namekeyword, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchByEmail(String emailkeyword, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchByPhoneNumber(String phoenNumberkeyword, int size, int page, String sortBy, String order,
            User user);

    List<Contact> getAllByUser(User user);

    long countByUser(User user);

    long countFavouriteByUser(User user);

    List<Contact> getFavouriteContactsByUser(User user);

    Page<Contact> getContactsByUserAndCategory(User user, String type, Pageable pageable);

}