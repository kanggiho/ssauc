package com.example.ssauc.user.login.repository;

import com.example.ssauc.user.login.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
  Users findByEmail(String email);
  Optional<Users> findByUserName(String userName);

  @Transactional
  @Modifying
  @Query("update Users u set u.cash = u.cash + ?1 where u.userId = ?2")
  void addCash(Long cash, Long userId);

  @Transactional
  @Modifying
  @Query("update Users u set u.cash = u.cash - ?1 where u.userId = ?2")
  void minusCash(Long cash, Long userId);


}