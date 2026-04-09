package com.library.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}