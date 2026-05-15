package com.scm.Repositories;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.entities.Contact;
import com.scm.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
        // find the contact by user
        // custom finder method
        Page<Contact> findByUser(User user, Pageable pageable);

        // custom query method
        @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
        List<Contact> findByUserId(@Param("userId") String userId);

        @Query("SELECT c FROM Contact c WHERE c.user = :user AND LOWER(REPLACE(c.name, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:namekeyword, ' ', ''), '%'))")
        Page<Contact> findByUserAndNameContaining(@Param("user") User user, @Param("namekeyword") String namekeyword,
                        Pageable pageable);

        // 2. For Email
        @Query("SELECT c FROM Contact c WHERE c.user = :user AND LOWER(REPLACE(c.email, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:namekeyword, ' ', ''), '%'))")
        Page<Contact> findByUserAndEmailContaining(@Param("user") User user, @Param("namekeyword") String namekeyword,
                        Pageable pageable);

        // 3. For Phone Number (Lower case not needed for numbers, but REPLACE handles
        // the spaces)
        @Query("SELECT c FROM Contact c WHERE c.user = :user AND REPLACE(c.phoneNumber, ' ', '') LIKE CONCAT('%', REPLACE(:namekeyword, ' ', ''), '%')")
        Page<Contact> findByUserAndPhoneNumberContaining(@Param("user") User user,
                        @Param("namekeyword") String namekeyword,
                        Pageable pageable);

        List<Contact> findByUser(User user);

        long countByUser(User user);

        long countByUserAndFavouriteTrue(User user);

}