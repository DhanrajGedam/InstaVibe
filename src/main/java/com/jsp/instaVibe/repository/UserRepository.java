package com.jsp.instaVibe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.instaVibe.dto.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);
	boolean existsByMobile(long mobile);
	boolean existsByUsername(String username);
	User findByUsername(String username);
}
