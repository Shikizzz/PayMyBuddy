package com.p6.payMyBuddy.repository;

import com.p6.payMyBuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, PagingAndSortingRepository<User, String> {
}
