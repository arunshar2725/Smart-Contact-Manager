package com.scm.Services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.Repositories.ContactRepo;
import com.scm.Services.ContactService;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.ResourceNotFoundException;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contact save(Contact contact) {
        // Check karo ki agar contact naya hai (ID null ya khali hai), tabhi nayi UUID
        // generate ho
        if (contact.getId() == null || contact.getId().isEmpty()) {
            String contactId = UUID.randomUUID().toString();
            contact.setId(contactId);
        }

        // Agar ID pehle se hai, toh Spring Data JPA apne aap samajh jayega ki UPDATE
        // karna hai
        return contactRepo.save(contact);
    }

    @Override
    public Contact update(Contact contact) {
        var contactOld = contactRepo.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setAddress(contact.getAddress());
        contactOld.setDescription(contact.getDescription());
        contactOld.setPicture(contact.getPicture());
        contactOld.setFavourite(contact.isFavourite());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        // contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactRepo.save(contactOld);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));

    }

    @Override
    public void delete(String id) {

        var contact = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));

        contactRepo.delete(contact);
    }

    @Override
    public List<Contact> getByUserId(String userId) {

        return contactRepo.findByUserId(userId);

    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByUser(user, pageable);

    }

    @Override
    public Page<Contact> searchByName(String namekeyword, int size, int page, String sortBy, String order, User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndNameContaining(user, namekeyword, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String emailkeyword, int size, int page, String sortBy, String order,
            User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndEmailContaining(user, emailkeyword, pageable);

    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoenNumberkeyword, int size, int page, String sortBy,
            String order, User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndPhoneNumberContaining(user, phoenNumberkeyword, pageable);

    }

    @Override
    public List<Contact> getAllByUser(User user) {
        return contactRepo.findByUser(user);
    }

    @Override
    public long countByUser(User user) {
        return contactRepo.countByUser(user);
    }

    @Override
    public long countFavouriteByUser(User user) {
        return contactRepo.countByUserAndFavouriteTrue(user);
    }
}
