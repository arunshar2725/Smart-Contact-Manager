package com.scm.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.scm.forms.FeedbackForm;

@Repository
public interface FeedbackRepo extends JpaRepository<FeedbackForm, Long> {

}
