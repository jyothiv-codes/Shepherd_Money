package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Crud Repository to store User classes
 */
@Repository("UserRepo")
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
}
