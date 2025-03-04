package com.example.ssauc.user.login.repository;

import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
  Users findByEmail(String email);
  Optional<Users> findByUserName(String userName);
}